package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.MedicamentDTO;
import com.proxymedoc.backend.mapper.EntityDTOMapper;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.repository.MedicamentRepository;
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
    private final EntityDTOMapper mapper;

    public MedicamentController(MedicamentRepository medicamentRepository, EntityDTOMapper mapper) {
        this.medicamentRepository = medicamentRepository;
        this.mapper = mapper;
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
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "notice", required = false) MultipartFile notice) throws IOException {

        Medicament medicament = new Medicament();
        medicament.setDenomination(denomination);
        medicament.setCategorie(categorie);
        medicament.setDescription(description);
        medicament.setPrixUnitaire(prixUnitaire);
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
        return ResponseEntity.ok(mapper.toMedicamentDTO(saved));
    }

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
     * Search medications by category
     * GET /api/medicaments/category/antibiotique
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MedicamentDTO>> searchByCategory(@PathVariable String category) {
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
        List<MedicamentDTO> result = medicamentRepository.findAll().stream()
                .filter(m -> m.getPrixUnitaire() >= minPrice && m.getPrixUnitaire() <= maxPrice)
                .map(mapper::toMedicamentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
