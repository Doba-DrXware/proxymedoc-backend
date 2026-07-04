package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.CommandeDTO;
import com.proxymedoc.backend.model.*;
import com.proxymedoc.backend.repository.MedicamentRepository;
import com.proxymedoc.backend.repository.PharmacieRepository;
import com.proxymedoc.backend.repository.UtilisateurRepository;
import com.proxymedoc.backend.service.CommandeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "http://localhost:3000")
public class CommandeController {

    private final CommandeService commandeService;
    private final UtilisateurRepository utilisateurRepository;
    private final PharmacieRepository pharmacieRepository;
    private final MedicamentRepository medicamentRepository;

    public CommandeController(CommandeService commandeService, UtilisateurRepository utilisateurRepository, 
                           PharmacieRepository pharmacieRepository, MedicamentRepository medicamentRepository) {
        this.commandeService = commandeService;
        this.utilisateurRepository = utilisateurRepository;
        this.pharmacieRepository = pharmacieRepository;
        this.medicamentRepository = medicamentRepository;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@Valid @RequestBody CommandeDTO dto) {
        Utilisateur patient = utilisateurRepository.findById(dto.getPatientId()).orElse(null);
        Pharmacie pharmacie = pharmacieRepository.findById(dto.getPharmacieId()).orElse(null);

        if (patient == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
        }
        if (pharmacie == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Pharmacy not found"));
        }

        Commande cmd = new Commande();
        cmd.setPatient(patient);
        cmd.setPharmacie(pharmacie);
        cmd.setTypeCommande(TypeCommande.valueOf(dto.getTypeCommande() != null ? dto.getTypeCommande() : "STANDARD"));
        cmd.setDateCommande(LocalDateTime.now());
        cmd.setStatut(StatutCommande.EN_ATTENTE);

        // map lignes
        if (dto.getLignes() != null && !dto.getLignes().isEmpty()) {
            for (var lp_dto : dto.getLignes()) {
                Medicament med = medicamentRepository.findById(lp_dto.getMedicamentId()).orElse(null);
                if (med != null) {
                    LignePanier lp = new LignePanier();
                    lp.setMedicament(med);
                    lp.setQuantite(lp_dto.getQuantite());
                    lp.setPrixUnitaire(lp_dto.getPrixUnitaire());
                    lp.setCommande(cmd);
                    cmd.getLignes().add(lp);
                }
            }
        }

        Commande saved = commandeService.create(cmd);
        return ResponseEntity.ok(Map.of("id", saved.getId(), "montantTotal", saved.getMontantTotal(), "statut", saved.getStatut()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Commande> list() {
        return List.of();
    }
}
