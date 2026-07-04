package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.ReservationDTO;
import com.proxymedoc.backend.model.*;
import com.proxymedoc.backend.repository.MedicamentRepository;
import com.proxymedoc.backend.repository.UtilisateurRepository;
import com.proxymedoc.backend.repository.PharmacieRepository;
import com.proxymedoc.backend.repository.ReservationRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MedicamentRepository medicamentRepository;
    private final PharmacieRepository pharmacieRepository;

    public ReservationController(ReservationRepository reservationRepository, UtilisateurRepository utilisateurRepository,
                              MedicamentRepository medicamentRepository, PharmacieRepository pharmacieRepository) {
        this.reservationRepository = reservationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.medicamentRepository = medicamentRepository;
        this.pharmacieRepository = pharmacieRepository;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@Valid @RequestBody ReservationDTO dto) {
        Utilisateur patient = utilisateurRepository.findById(dto.getPatientId()).orElse(null);
        Medicament med = medicamentRepository.findById(dto.getMedicamentId()).orElse(null);
        Pharmacie pharmacie = pharmacieRepository.findById(dto.getPharmacieId()).orElse(null);

        if (patient == null || med == null || pharmacie == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Patient, Medicament or Pharmacy not found"));
        }

        Reservation res = new Reservation();
        res.setPatient(patient);
        res.setMedicament(med);
        res.setPharmacie(pharmacie);
        res.setDateReservation(LocalDate.now());
        res.setDateExpiration(dto.getDateExpiration() != null ? dto.getDateExpiration() : LocalDate.now().plusDays(7));
        res.setStatut(StatutReservation.EN_ATTENTE);

        Reservation saved = reservationRepository.save(res);
        return ResponseEntity.ok(Map.of("id", saved.getId(), "statut", saved.getStatut()));
    }
}
