package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.PharmacieDTO;
import com.proxymedoc.backend.mapper.EntityDTOMapper;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.model.Pharmacie;
import com.proxymedoc.backend.model.Pharmacien;
import com.proxymedoc.backend.model.Stock;
import com.proxymedoc.backend.model.Utilisateur;
import com.proxymedoc.backend.security.SecurityUtil;
import com.proxymedoc.backend.service.PharmacieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Pharmacie> pharmacies = pharmacieService.findAll().stream()
                .filter(p -> p.getStatut() == null || p.getStatut() != com.proxymedoc.backend.model.StatutPharmacie.SUSPENDUE)
                .filter(p -> lat == null || lon == null || pharmacieService.isWithinRadius(p.getLatitude(), p.getLongitude(), lat, lon, radius))
                .filter(p -> q == null || q.isBlank() || matchesQuery(p, q))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = pharmacies.stream().map(this::toFrontendPayload).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private boolean matchesQuery(Pharmacie pharmacie, String query) {
        String normalizedQuery = normalize(query);
        if (pharmacie.getNom() != null && normalize(pharmacie.getNom()).contains(normalizedQuery)) {
            return true;
        }
        if (pharmacie.getStocks() == null) {
            return false;
        }
        return pharmacie.getStocks().stream().anyMatch(stock -> {
            Medicament medicament = stock.getMedicament();
            return medicament != null
                    && medicament.getDenomination() != null
                    && normalize(medicament.getDenomination()).contains(normalizedQuery);
        });
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
                medPayload.put("prix", medicament.getPrixUnitaire());
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
