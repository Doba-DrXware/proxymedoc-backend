package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.OrdonnanceDTO;
import com.proxymedoc.backend.model.*;
import com.proxymedoc.backend.repository.OrdonnanceRepository;
import com.proxymedoc.backend.repository.UtilisateurRepository;
import com.proxymedoc.backend.repository.PharmacieRepository;
import com.proxymedoc.backend.service.OrdonnanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ordonnances")
@CrossOrigin(origins = "http://localhost:3000")
public class OrdonnanceController {

    private final OrdonnanceService ordonnanceService;
    private final OrdonnanceRepository ordonnanceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PharmacieRepository pharmacieRepository;

    public OrdonnanceController(OrdonnanceService ordonnanceService, OrdonnanceRepository ordonnanceRepository,
                             UtilisateurRepository utilisateurRepository, PharmacieRepository pharmacieRepository) {
        this.ordonnanceService = ordonnanceService;
        this.ordonnanceRepository = ordonnanceRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.pharmacieRepository = pharmacieRepository;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@Valid @RequestBody OrdonnanceDTO dto) {
        Utilisateur patient = utilisateurRepository.findById(dto.getPatientId()).orElse(null);
        Pharmacie pharmacie = pharmacieRepository.findById(dto.getPharmacieId()).orElse(null);

        if (patient == null || pharmacie == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Patient or Pharmacy not found"));
        }

        Ordonnance ord = new Ordonnance();
        ord.setPatient(patient);
        ord.setPharmacie(pharmacie);
        ord.setFichierUrl(dto.getFichierUrl());
        ord.setDocumentLegalUrl(dto.getDocumentLegalUrl());
        ord.setPhoto1Url(dto.getPhoto1Url());
        ord.setPhoto2Url(dto.getPhoto2Url());
        ord.setPhoto3Url(dto.getPhoto3Url());
        ord.setCommentairePharmacie(dto.getCommentairePharmacie());
        ord.setStatut(StatutOrdonnance.EN_ATTENTE);

        Ordonnance saved = ordonnanceRepository.save(ord);
        return ResponseEntity.ok(Map.of("id", saved.getId(), "statut", saved.getStatut()));
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> validate(@PathVariable Long id) {
        Ordonnance ord = ordonnanceService.valider(id);
        if (ord != null) {
            return ResponseEntity.ok(Map.of("id", ord.getId(), "statut", ord.getStatut()));
        }
        return ResponseEntity.notFound().build();
    }
}
