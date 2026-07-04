package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.MedicamentDTO;
import com.proxymedoc.backend.mapper.EntityDTOMapper;
import com.proxymedoc.backend.model.Medicament;
import com.proxymedoc.backend.repository.MedicamentRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
                .filter(m -> (m.getNom() != null && m.getNom().toLowerCase().contains(query)) ||
                            (m.getCategorie() != null && m.getCategorie().toLowerCase().contains(query)) ||
                            (m.getDenomination() != null && m.getDenomination().toLowerCase().contains(query)))
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
