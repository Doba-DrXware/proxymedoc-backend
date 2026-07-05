package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.model.Role;
import com.proxymedoc.backend.repository.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UtilisateurRepository utilisateurRepository;

    public AdminController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        long patients = utilisateurRepository.countByRole(Role.PATIENT);
        long searches30d = 0L;

        return ResponseEntity.ok(Map.of(
                "patients", patients,
                "searches30d", searches30d
        ));
    }
}
