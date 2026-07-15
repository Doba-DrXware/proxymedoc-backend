package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.MedicamentDTO;
import com.proxymedoc.backend.dto.MedicamentSearchDTO;
import com.proxymedoc.backend.mapper.EntityDTOMapper;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.model.Pharmacie;
import com.proxymedoc.backend.model.Pharmacien;
import com.proxymedoc.backend.model.Stock;
import com.proxymedoc.backend.model.Utilisateur;
import com.proxymedoc.backend.repository.MedicamentRepository;
import com.proxymedoc.backend.repository.StockRepository;
import com.proxymedoc.backend.security.SecurityUtil;
import com.proxymedoc.backend.service.MedicamentSearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicaments")
@CrossOrigin(origins = "http://localhost:3000")
public class MedicamentController {

    private final MedicamentRepository medicamentRepository;
    private final StockRepository stockRepository;
    private final EntityDTOMapper mapper;
    private final SecurityUtil securityUtil;
    private final MedicamentSearchService medicamentSearchService;

    public MedicamentController(MedicamentRepository medicamentRepository, StockRepository stockRepository, EntityDTOMapper mapper, SecurityUtil securityUtil, MedicamentSearchService medicamentSearchService) {
        this.medicamentRepository = medicamentRepository;
        this.stockRepository = stockRepository;
        this.mapper = mapper;
        this.securityUtil = securityUtil;
        this.medicamentSearchService = medicamentSearchService;
    }

    @GetMapping
    public ResponseEntity<List<MedicamentDTO>> list() {
        List<MedicamentDTO> result = medicamentRepository.findAll().stream()
                .map(mapper::toMedicamentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentDTO> getOne(@PathVariable Long id) {
        Medicament m = medicamentRepository.findById(id).orElse(null);
        if (m != null) {
            return ResponseEntity.ok(mapper.toMedicamentDTO(m));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MedicamentDTO dto) {
        Medicament m = mapper.toMedicament(dto);
        Medicament saved = medicamentRepository.save(m);
        createStockForCurrentPharmacy(saved, null, dto.getPrixUnitaire());
        return ResponseEntity.ok(mapper.toMedicamentDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody MedicamentDTO dto) {
        Medicament existing = medicamentRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setDenomination(dto.getDenomination());
        existing.setCategorie(dto.getCategorie());
        existing.setDescription(dto.getDescription());
        existing.setFormeGalenique(dto.getFormeGalenique());
        existing.setDosage(dto.getDosage());
        existing.setExigeOrdonnance(dto.getExigeOrdonnance());
        existing.setImageUrl(dto.getImageUrl());
        existing.setNoticeUrl(dto.getNoticeUrl());

        Medicament saved = medicamentRepository.save(existing);
        return ResponseEntity.ok(mapper.toMedicamentDTO(saved));
    }

    @PostMapping(value = "/with-files", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createWithFiles(
            @RequestParam("denomination") String denomination,
            @RequestParam(value = "categorie", required = false) String categorie,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "prixUnitaire", required = false) Double prixUnitaire,
            @RequestParam(value = "formeGalenique", required = false) String formeGalenique,
            @RequestParam(value = "dosage", required = false) String dosage,
            @RequestParam(value = "exigeOrdonnance", required = false) Boolean exigeOrdonnance,
            @RequestParam(value = "stock", required = false) Integer stock,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "notice", required = false) MultipartFile notice) throws IOException {

        Medicament medicament = new Medicament();
        medicament.setDenomination(denomination);
        medicament.setCategorie(categorie);
        medicament.setDescription(description);
        medicament.setFormeGalenique(formeGalenique);
        medicament.setDosage(dosage);
        medicament.setExigeOrdonnance(Boolean.TRUE.equals(exigeOrdonnance));

        if (photo != null && !photo.isEmpty()) {
            medicament.setImageUrl(storeFile(photo, "images"));
        }
        if (notice != null && !notice.isEmpty()) {
            medicament.setNoticeUrl(storeFile(notice, "notices"));
        }

        Medicament saved = medicamentRepository.save(medicament);
        createStockForCurrentPharmacy(saved, stock, prixUnitaire);
        return ResponseEntity.ok(mapper.toMedicamentDTO(saved));
    }

    private void createStockForCurrentPharmacy(Medicament medicament, Integer quantity, Double prixUnitaire) {
        Utilisateur currentUser = securityUtil.getCurrentUser();
        if (currentUser instanceof Pharmacien pharmacist && pharmacist.getPharmacie() != null) {
            Pharmacie pharmacy = pharmacist.getPharmacie();
            Stock stock = new Stock();
            stock.setMedicament(medicament);
            stock.setPharmacie(pharmacy);
            stock.setQuantiteDisponible(quantity != null ? quantity : 0);
            stock.setPrixUnitaire(prixUnitaire != null ? prixUnitaire : 0.0);
            stock.setSeuilAlerte(0);
            stock.setDateMAJ(java.time.LocalDate.now());
            stockRepository.save(stock);
        }
    }

    @PutMapping("/{medicamentId}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long medicamentId, @RequestBody StockUpdateRequest request) {
        Utilisateur currentUser = securityUtil.getCurrentUser();
        if (!(currentUser instanceof Pharmacien pharmacist) || pharmacist.getPharmacie() == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "Non authentifié"));
        }

        Medicament medicament = medicamentRepository.findById(medicamentId).orElse(null);
        if (medicament == null) {
            return ResponseEntity.notFound().build();
        }

        Stock stock = stockRepository.findAll().stream()
                .filter(item -> item.getMedicament() != null && item.getMedicament().getId().equals(medicamentId))
                .filter(item -> item.getPharmacie() != null && item.getPharmacie().getId().equals(pharmacist.getPharmacie().getId()))
                .findFirst()
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setMedicament(medicament);
                    newStock.setPharmacie(pharmacist.getPharmacie());
                    return newStock;
                });

        if (request != null) {
            if (request.quantiteDisponible() != null) {
                stock.setQuantiteDisponible(request.quantiteDisponible());
            }
            if (request.prixUnitaire() != null) {
                stock.setPrixUnitaire(request.prixUnitaire());
            }
        }
        stock.setSeuilAlerte(0);
        stock.setDateMAJ(java.time.LocalDate.now());
        stockRepository.save(stock);

        return ResponseEntity.ok(java.util.Map.of("success", true, "stock", stock.getQuantiteDisponible()));
    }

    @DeleteMapping("/{medicamentId}/stock")
    public ResponseEntity<?> deleteStock(@PathVariable Long medicamentId) {
        Utilisateur currentUser = securityUtil.getCurrentUser();
        if (!(currentUser instanceof Pharmacien pharmacist) || pharmacist.getPharmacie() == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "Non authentifié"));
        }

        Medicament medicament = medicamentRepository.findById(medicamentId).orElse(null);
        if (medicament == null) {
            return ResponseEntity.notFound().build();
        }

        Stock stock = stockRepository.findAll().stream()
                .filter(item -> item.getMedicament() != null && item.getMedicament().getId().equals(medicamentId))
                .filter(item -> item.getPharmacie() != null && item.getPharmacie().getId().equals(pharmacist.getPharmacie().getId()))
                .findFirst()
                .orElse(null);

        if (stock == null) {
            return ResponseEntity.status(404).body(java.util.Map.of("message", "Stock introuvable pour ce médicament et cette pharmacie."));
        }

        stockRepository.delete(stock);
        return ResponseEntity.noContent().build();
    }

    public record StockUpdateRequest(Integer quantiteDisponible, Double prixUnitaire) {}

    private String storeFile(MultipartFile file, String folderName) throws IOException {
        try {
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
            }

            Path uploadDir = Paths.get("uploads", "medicaments", folderName).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            String storedName = UUID.randomUUID() + extension;
            Path target = uploadDir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/medicaments/" + folderName + "/" + storedName;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to store uploaded file: " + e.getMessage(), e);
        }
    }

    /**
     * Search medications by name or category
     * GET /api/medicaments/search?q=Amoxicilline
     */
    @GetMapping("/search")
    public ResponseEntity<List<MedicamentDTO>> search(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String query = q.toLowerCase();
        List<MedicamentDTO> result = medicamentRepository.findAll().stream()
                .filter(m -> (m.getDenomination() != null && m.getDenomination().toLowerCase().contains(query)) ||
                            (m.getCategorie() != null && m.getCategorie().toLowerCase().contains(query)))
                .map(mapper::toMedicamentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Advanced search for medications based on multiple criteria.
     * This endpoint searches through denomination, dosage, forme galénique, and category.
     * Returns essential information needed for the patient to identify medications,
     * which will be later used to find the best pharmacies based on multicriteria optimization.
     *
     * GET /api/medicaments/search-advanced?q=Paracétamol%20500mg
     * GET /api/medicaments/search-advanced?q=comprimé
     *
     * @param q Search query from the patient (can include medication name, dosage, form, or category)
     * @return List of matching medications with essential details (id, denomination, category, dosage, forme_galeniqueXML
     */
    @GetMapping("/search-advanced")
    public ResponseEntity<List<MedicamentSearchDTO>> searchAdvanced(@RequestParam(name = "q", required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<MedicamentSearchDTO> results = medicamentSearchService.searchMedicaments(query);
        
        if (results.isEmpty()) {
            // Retourner une liste vide avec le code 200 (pas d'erreur, juste aucun résultat)
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(results);
    }

    /**
     * Search medications by dosage.
     * GET /api/medicaments/search-by-dosage?dosage=500mg
     *
     * @param dosage Dosage to search for
     * @return List of medications with this dosage
     */
    @GetMapping("/search-by-dosage")
    public ResponseEntity<List<MedicamentSearchDTO>> searchByDosage(@RequestParam String dosage) {
        if (dosage == null || dosage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<MedicamentSearchDTO> results = medicamentSearchService.searchByDosage(dosage);
        return ResponseEntity.ok(results);
    }

    /**
     * Search medications by pharmaceutical form.
     * GET /api/medicaments/search-by-forme?forme=Comprimé
     *
     * @param forme Pharmaceutical form to search for
     * @return List of medications with this form
     */
    @GetMapping("/search-by-forme")
    public ResponseEntity<List<MedicamentSearchDTO>> searchByFormeGalenique(@RequestParam String forme) {
        if (forme == null || forme.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<MedicamentSearchDTO> results = medicamentSearchService.searchByFormeGalenique(forme);
        return ResponseEntity.ok(results);
    }

    /**
     * Search medications by category.
     * GET /api/medicaments/search-by-category?category=Analgésique
     *
     * @param category Category to search for
     * @return List of medications in this category
     */
    @GetMapping("/search-by-category")
    public ResponseEntity<List<MedicamentSearchDTO>> searchByCategory(@RequestParam String category) {
        if (category == null || category.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<MedicamentSearchDTO> results = medicamentSearchService.searchByCategorie(category);
        return ResponseEntity.ok(results);
    }

    /**
     * Search medications by category (legacy endpoint)
     * GET /api/medicaments/category/antibiotique
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MedicamentDTO>> searchByCategoryLegacy(@PathVariable String category) {
        List<MedicamentDTO> result = medicamentRepository.findAll().stream()
                .filter(m -> m.getCategorie() != null && m.getCategorie().equalsIgnoreCase(category))
                .map(mapper::toMedicamentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Search medications that require prescription
     * GET /api/medicaments/requires-prescription
     */
    @GetMapping("/requires-prescription")
    public ResponseEntity<List<MedicamentDTO>> requiresPrescription() {
        List<MedicamentDTO> result = medicamentRepository.findAll().stream()
                .filter(m -> m.getExigeOrdonnance() != null && m.getExigeOrdonnance())
                .map(mapper::toMedicamentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Search medications by price range
     * GET /api/medicaments/price-range?min=100&max=500
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<MedicamentDTO>> searchByPriceRange(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max) {
        Double minPrice = min != null ? min : 0.0;
        Double maxPrice = max != null ? max : Double.MAX_VALUE;
        List<MedicamentDTO> result = stockRepository.findAll().stream()
                .filter(stock -> stock.getPrixUnitaire() != null && stock.getPrixUnitaire() >= minPrice && stock.getPrixUnitaire() <= maxPrice)
                .map(Stock::getMedicament)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(mapper::toMedicamentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
