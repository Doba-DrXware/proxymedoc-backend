package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.PharmacieDTO;
import com.proxymedoc.backend.mapper.EntityDTOMapper;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.model.Pharmacie;
import com.proxymedoc.backend.model.Pharmacien;
import com.proxymedoc.backend.model.StatutPharmacie;
import com.proxymedoc.backend.model.Stock;
import com.proxymedoc.backend.model.Utilisateur;
import com.proxymedoc.backend.security.SecurityUtil;
import com.proxymedoc.backend.service.PharmacieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/pharmacies")
@CrossOrigin(origins = "http://localhost:3000")
public class PharmacieController {

    private final PharmacieService pharmacieService;
    private final EntityDTOMapper mapper;
    private final SecurityUtil securityUtil;

    public PharmacieController(PharmacieService pharmacieService, EntityDTOMapper mapper, SecurityUtil securityUtil) {
        this.pharmacieService = pharmacieService;
        this.mapper = mapper;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    public ResponseEntity<List<PharmacieDTO>> list() {
        List<PharmacieDTO> result = pharmacieService.findAll().stream()
                .map(mapper::toPharmacieDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacieDTO> getOne(@PathVariable Long id) {
        Pharmacie p = pharmacieService.findById(id);
        if (p != null) {
            return ResponseEntity.ok(mapper.toPharmacieDTO(p));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PharmacieDTO dto) {
        Pharmacie p = mapper.toPharmace(dto);
        Pharmacie saved = pharmacieService.save(p);
        return ResponseEntity.ok(mapper.toPharmacieDTO(saved));
    }

    /**
     * Search pharmacies by name
     * GET /api/pharmacies/search/name?q=Palais
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<PharmacieDTO>> searchByName(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<PharmacieDTO> result = pharmacieService.searchByName(q).stream()
                .map(mapper::toPharmacieDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Search pharmacies nearby
     * GET /api/pharmacies/search/nearby?lat=3.8667&lon=11.5167&radius=10
     * radius in km
     */
    @GetMapping("/search/nearby")
    public ResponseEntity<List<PharmacieDTO>> searchNearby(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "10") Double radius) {
        if (lat == null || lon == null) {
            return ResponseEntity.badRequest().build();
        }
        List<PharmacieDTO> result = pharmacieService.searchNearby(lat, lon, radius).stream()
                .map(mapper::toPharmacieDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Get only validated pharmacies
     * GET /api/pharmacies/validated
     */
    @GetMapping("/validated")
    public ResponseEntity<List<PharmacieDTO>> getValidated() {
        List<PharmacieDTO> result = pharmacieService.findValidated().stream()
                .map(mapper::toPharmacieDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/with-stocks")
    public ResponseEntity<List<Map<String, Object>>> listWithStocks() {
        List<Map<String, Object>> result = pharmacieService.findAll().stream().map(this::toFrontendPayload).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Pharmacie pharmacy = pharmacieService.findById(id);
        if (pharmacy == null) {
            return ResponseEntity.notFound().build();
        }

        Object statutValue = payload.get("statut");
        if (statutValue == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Le statut est requis"));
        }

        String statut = String.valueOf(statutValue).trim().toUpperCase(Locale.ROOT);
        switch (statut) {
            case "VALIDEE" -> pharmacy.setStatut(StatutPharmacie.VALIDEE);
            case "SUSPENDUE" -> pharmacy.setStatut(StatutPharmacie.SUSPENDUE);
            case "REJETEE" -> pharmacy.setStatut(StatutPharmacie.REJETEE);
            case "EN_ATTENTE" -> pharmacy.setStatut(StatutPharmacie.EN_ATTENTE);
            default -> {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Statut invalide"));
            }
        }

        Pharmacie saved = pharmacieService.save(pharmacy);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Statut mis à jour",
                "pharmacy", toFrontendPayload(saved)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyPharmacy() {
        Utilisateur user = securityUtil.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Non authentifié"));
        }

        if (!(user instanceof Pharmacien pharmacist) || pharmacist.getPharmacie() == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Aucune pharmacie associée à ce compte"));
        }

        return ResponseEntity.ok(toFrontendPayload(pharmacist.getPharmacie()));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyPharmacy(@RequestBody Map<String, Object> payload) {
        Utilisateur user = securityUtil.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Non authentifié"));
        }

        if (!(user instanceof Pharmacien pharmacist) || pharmacist.getPharmacie() == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Aucune pharmacie associée à ce compte"));
        }

        Pharmacie pharmacy = pharmacist.getPharmacie();

        if (payload.containsKey("horaires")) {
            Object horaires = payload.get("horaires");
            pharmacy.setHoraires(horaires == null ? null : String.valueOf(horaires));
        }

        if (payload.containsKey("telephone")) {
            Object telephone = payload.get("telephone");
            pharmacy.setTelephone(telephone == null ? null : String.valueOf(telephone));
        }

        if (payload.containsKey("latitude")) {
            Object latitude = payload.get("latitude");
            if (latitude instanceof Number number) {
                pharmacy.setLatitude(number.doubleValue());
            } else if (latitude != null && !String.valueOf(latitude).isBlank()) {
                pharmacy.setLatitude(Double.parseDouble(String.valueOf(latitude)));
            }
        }

        if (payload.containsKey("longitude")) {
            Object longitude = payload.get("longitude");
            if (longitude instanceof Number number) {
                pharmacy.setLongitude(number.doubleValue());
            } else if (longitude != null && !String.valueOf(longitude).isBlank()) {
                pharmacy.setLongitude(Double.parseDouble(String.valueOf(longitude)));
            }
        }

        if (payload.containsKey("estDeGarde")) {
            Object estDeGarde = payload.get("estDeGarde");
            if (estDeGarde instanceof Boolean bool) {
                pharmacy.setEstDeGarde(bool);
            } else if (estDeGarde != null) {
                pharmacy.setEstDeGarde(Boolean.parseBoolean(String.valueOf(estDeGarde)));
            }
        }

        Pharmacie saved = pharmacieService.save(pharmacy);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Informations pharmacie mises à jour",
            "pharmacy", toFrontendPayload(saved)
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchPharmacies(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "10") Double radius) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        String normalizedQuery = normalize(q);
        List<String> queryTokens = Arrays.stream(normalizedQuery.split("\\s+"))
                .filter(token -> !token.isBlank())
                .toList();
        List<String> queryNumbers = extractNumberTokens(normalizedQuery);

        List<Map<String, Object>> result = pharmacieService.findAll().stream()
                .filter(p -> p.getStatut() == null || p.getStatut() != StatutPharmacie.SUSPENDUE)
                .filter(p -> lat == null || lon == null || pharmacieService.isWithinRadius(p.getLatitude(), p.getLongitude(), lat, lon, radius))
                .flatMap(p -> {
                    if (p.getStocks() == null) {
                        return Stream.empty();
                    }
                    return p.getStocks().stream()
                            .map(stock -> {
                                Medicament medicament = stock.getMedicament();
                                if (medicament == null || medicament.getDenomination() == null) {
                                    return null;
                                }

                                boolean denominationMatch = matchesDenomination(medicament.getDenomination(), normalizedQuery, queryTokens);
                                if (!denominationMatch) {
                                    return null;
                                }

                                boolean dosageMatch = matchesDosage(medicament, queryNumbers);
                                Map<String, Object> entry = new HashMap<>();
                                entry.put("pharmacieId", p.getId());
                                entry.put("pharmacieNom", p.getNom());
                                entry.put("adresse", p.getAdresse());
                                entry.put("telephone", p.getTelephone());
                                entry.put("horaires", p.getHoraires());
                                entry.put("garde", Boolean.TRUE.equals(p.getEstDeGarde()));
                                entry.put("statut", p.getStatut() == null ? "attente" : switch (p.getStatut()) {
                                    case VALIDEE -> "active";
                                    case SUSPENDUE -> "suspendue";
                                    case REJETEE -> "rejetee";
                                    default -> "attente";
                                });
                                entry.put("score_ia", p.getScoreIa() != null ? p.getScoreIa() : 0);
                                entry.put("latitude", p.getLatitude());
                                entry.put("longitude", p.getLongitude());
                                entry.put("distance", 0);
                                entry.put("dosageMatch", dosageMatch);

                                Map<String, Object> medPayload = new HashMap<>();
                                medPayload.put("id", medicament.getId());
                                medPayload.put("nom", medicament.getDenomination());
                                medPayload.put("denomination", medicament.getDenomination());
                                medPayload.put("prix", stock.getPrixUnitaire());
                                medPayload.put("stock", stock.getQuantiteDisponible());
                                medPayload.put("description", medicament.getDescription());
                                medPayload.put("dispo", stock.getQuantiteDisponible() != null && stock.getQuantiteDisponible() > 0);
                                medPayload.put("image", medicament.getImageUrl());
                                medPayload.put("imageUrl", medicament.getImageUrl());
                                medPayload.put("noticeUrl", medicament.getNoticeUrl());
                                medPayload.put("categorie", medicament.getCategorie());
                                medPayload.put("formeGalenique", medicament.getFormeGalenique());
                                medPayload.put("dosage", medicament.getDosage());
                                medPayload.put("exigeOrdonnance", medicament.getExigeOrdonnance());
                                entry.put("med", medPayload);

                                return entry;
                            });
                })
                .filter(Objects::nonNull)
                .sorted((left, right) -> {
                    boolean leftMatch = Boolean.TRUE.equals(left.get("dosageMatch"));
                    boolean rightMatch = Boolean.TRUE.equals(right.get("dosageMatch"));
                    return Boolean.compare(!leftMatch, !rightMatch);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    private boolean matchesDenomination(String denomination, String normalizedQuery, List<String> queryTokens) {
        String normalizedDenomination = normalize(denomination);
        if (normalizedDenomination.contains(normalizedQuery) || normalizedQuery.contains(normalizedDenomination)) {
            return true;
        }
        for (String token : queryTokens) {
            if (token.length() >= 3 && normalizedDenomination.contains(token)) {
                return true;
            }
        }
        return Arrays.stream(normalizedDenomination.split("\\s+"))
                .filter(token -> token.length() >= 3)
                .anyMatch(normalizedQuery::contains);
    }

    private boolean matchesDosage(Medicament medicament, List<String> queryNumbers) {
        if (queryNumbers.isEmpty() || medicament.getDosage() == null) {
            return false;
        }
        String normalizedDosage = normalize(medicament.getDosage());
        return queryNumbers.stream().anyMatch(normalizedDosage::contains);
    }

    private List<String> extractNumberTokens(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        List<String> numbers = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+").matcher(value);
        while (matcher.find()) {
            numbers.add(matcher.group());
        }
        return numbers;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }

    private Map<String, Object> toFrontendPayload(Pharmacie p) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", p.getId());
        payload.put("nom", p.getNom());
        payload.put("adresse", p.getAdresse());
        payload.put("telephone", p.getTelephone());
        payload.put("horaires", p.getHoraires());
        payload.put("garde", Boolean.TRUE.equals(p.getEstDeGarde()));
        payload.put("statut", p.getStatut() == null ? "attente" : switch (p.getStatut()) {
            case VALIDEE -> "active";
            case SUSPENDUE -> "suspendue";
            case REJETEE -> "rejetee";
            default -> "attente";
        });
        payload.put("score_ia", p.getScoreIa() != null ? p.getScoreIa() : 0);
        payload.put("latitude", p.getLatitude());
        payload.put("longitude", p.getLongitude());
        payload.put("contact", p.getContact());
        payload.put("licence", p.getNumeroLicence());
        payload.put("photo1Url", p.getPhoto1Url());
        payload.put("photo2Url", p.getPhoto2Url());
        payload.put("photo3Url", p.getPhoto3Url());

        List<String> imageUrls = new ArrayList<>();
        if (p.getPhoto1Url() != null && !p.getPhoto1Url().isBlank()) imageUrls.add(p.getPhoto1Url());
        if (p.getPhoto2Url() != null && !p.getPhoto2Url().isBlank()) imageUrls.add(p.getPhoto2Url());
        if (p.getPhoto3Url() != null && !p.getPhoto3Url().isBlank()) imageUrls.add(p.getPhoto3Url());
        payload.put("images", imageUrls);

        List<Map<String, Object>> meds = new ArrayList<>();
        if (p.getStocks() != null) {
            for (Stock stock : p.getStocks()) {
                Medicament medicament = stock.getMedicament();
                if (medicament == null) {
                    continue;
                }
                Map<String, Object> medPayload = new HashMap<>();
                medPayload.put("id", medicament.getId());
                medPayload.put("nom", medicament.getDenomination());
                medPayload.put("denomination", medicament.getDenomination());
                medPayload.put("prix", stock.getPrixUnitaire());
                medPayload.put("stock", stock.getQuantiteDisponible());
                medPayload.put("description", medicament.getDescription());
                medPayload.put("dispo", stock.getQuantiteDisponible() != null && stock.getQuantiteDisponible() > 0);
                medPayload.put("image", medicament.getImageUrl());
                medPayload.put("imageUrl", medicament.getImageUrl());
                medPayload.put("noticeUrl", medicament.getNoticeUrl());
                medPayload.put("categorie", medicament.getCategorie());
                medPayload.put("formeGalenique", medicament.getFormeGalenique());
                medPayload.put("dosage", medicament.getDosage());
                medPayload.put("exigeOrdonnance", medicament.getExigeOrdonnance());
                meds.add(medPayload);
            }
        }
        payload.put("meds", meds);
        return payload;
    }
}
