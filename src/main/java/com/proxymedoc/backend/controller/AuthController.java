package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.model.Pharmacie;
import com.proxymedoc.backend.model.Role;
import com.proxymedoc.backend.model.Utilisateur;
import com.proxymedoc.backend.model.Patient;
import com.proxymedoc.backend.model.Pharmacien;
import com.proxymedoc.backend.model.StatutPharmacie;
import com.proxymedoc.backend.repository.PharmacieRepository;
import com.proxymedoc.backend.repository.UtilisateurRepository;
import com.proxymedoc.backend.security.JwtAuthenticationResponse;
import com.proxymedoc.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PharmacieRepository pharmacieRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UtilisateurRepository utilisateurRepository, PharmacieRepository pharmacieRepository, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.pharmacieRepository = pharmacieRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
        String role = (String) payload.getOrDefault("role", "patient");
        String name = ((String) payload.getOrDefault("name", "")).trim();
        String email = ((String) payload.getOrDefault("email", "")).trim();
        String password = ((String) payload.getOrDefault("password", "demo1234")).trim();

        if (password.isEmpty()) {
            password = "demo1234";
        }

        if (name.isEmpty() || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Nom ou email manquant"));
        }

        if (utilisateurRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email déjà utilisé"));
        }

        Utilisateur user;
        if ("pharmacie".equalsIgnoreCase(role)) {
            Pharmacien pharm = new Pharmacien();
            pharm.setEmail(email);
            pharm.setNom(name);
            pharm.setPassword(passwordEncoder.encode(password));
            pharm.setNumeroLicence((String) payload.getOrDefault("licence", null));

            Pharmacie ph = new Pharmacie();
            ph.setNom(name);
            ph.setAdresse((String) payload.getOrDefault("adresse", ""));
            ph.setStatut(StatutPharmacie.EN_ATTENTE);
            ph.setTelephone((String) payload.getOrDefault("phone", ""));
            ph.setNumeroLicence((String) payload.getOrDefault("licence", null));
            pharmacieRepository.save(ph);

            pharm.setPharmacie(ph);
            user = pharm;
        } else {
            Patient p = new Patient();
            p.setEmail(email);
            p.setNom(name);
            p.setPassword(passwordEncoder.encode(password));
            user = p;
        }

        Utilisateur savedUser = utilisateurRepository.save(user);
        
        // Generate JWT token
        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());
        
        JwtAuthenticationResponse response = new JwtAuthenticationResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "User registered successfully",
            "token", response.getToken(),
            "type", response.getType(),
            "user", Map.of(
                "id", savedUser.getId(),
                "role", savedUser.getRole().name().toLowerCase(),
                "name", savedUser.getNom(),
                "email", savedUser.getEmail()
            )
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> payload) {
        String email = ((String) payload.getOrDefault("email", "")).trim();
        String password = ((String) payload.getOrDefault("password", "")).trim();

        if (email.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email ou mot de passe manquant"));
        }

        Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Identifiants invalides"));
        }

        Utilisateur user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Identifiants invalides"));
        }

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        JwtAuthenticationResponse response = new JwtAuthenticationResponse(token, user.getId(), user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Connexion réussie",
            "token", response.getToken(),
            "type", response.getType(),
            "user", Map.of(
                "id", user.getId(),
                "role", user.getRole().name().toLowerCase(),
                "name", user.getNom(),
                "email", user.getEmail()
            )
        ));
    }
}
