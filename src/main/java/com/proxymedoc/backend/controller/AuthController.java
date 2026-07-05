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
import com.proxymedoc.backend.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PharmacieRepository pharmacieRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    @Autowired
    public AuthController(UtilisateurRepository utilisateurRepository, PharmacieRepository pharmacieRepository, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder, SecurityUtil securityUtil) {
        this.utilisateurRepository = utilisateurRepository;
        this.pharmacieRepository = pharmacieRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> registerJson(@RequestBody Map<String, Object> payload) {
        return registerInternal(payload, null, null);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> registerMultipart(@RequestParam Map<String, String> formData,
                                               @RequestParam(value = "legalDocs", required = false) List<MultipartFile> legalDocs,
                                               @RequestParam(value = "pharmacyImages", required = false) List<MultipartFile> pharmacyImages) {
        Map<String, Object> payload = new java.util.HashMap<>();
        formData.forEach(payload::put);
        return registerInternal(payload, legalDocs, pharmacyImages);
    }

    private ResponseEntity<?> registerInternal(Map<String, Object> requestPayload,
                                               List<MultipartFile> legalDocs,
                                               List<MultipartFile> pharmacyImages) {
        String role = String.valueOf(requestPayload.getOrDefault("role", "patient"));
        String nom = ((String) requestPayload.getOrDefault("nom", requestPayload.getOrDefault("name", ""))).trim();
        String prenom = ((String) requestPayload.getOrDefault("prenom", "")).trim();
        String email = ((String) requestPayload.getOrDefault("email", "")).trim();
        String password = ((String) requestPayload.getOrDefault("password", "demo1234")).trim();
        String adresse = ((String) requestPayload.getOrDefault("adresse", "")).trim();
        String phone = ((String) requestPayload.getOrDefault("phone", "")).trim();
        String pharmacyName = ((String) requestPayload.getOrDefault("pharmacyName", (prenom + " " + nom).trim())).trim();
        String pharmacyPhone = ((String) requestPayload.getOrDefault("pharmacyPhone", phone)).trim();
        String licence = ((String) requestPayload.getOrDefault("licence", "")).trim();
        String latitudeValue = String.valueOf(requestPayload.getOrDefault("latitude", "")).trim();
        String longitudeValue = String.valueOf(requestPayload.getOrDefault("longitude", "")).trim();
        String horaires = ((String) requestPayload.getOrDefault("horaires", "")).trim();
        boolean estDeGarde = Boolean.parseBoolean(String.valueOf(requestPayload.getOrDefault("estDeGarde", false)));
        String contact = ((String) requestPayload.getOrDefault("contact", "")).trim();

        if (password.isEmpty()) {
            password = "demo1234";
        }

        if ((nom + " " + prenom).trim().isEmpty() || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Nom, prénom ou email manquant"));
        }

        if (utilisateurRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email déjà utilisé"));
        }

        Utilisateur user;
        if ("pharmacie".equalsIgnoreCase(role)) {
            Pharmacien pharm = new Pharmacien();
            pharm.setEmail(email);
            pharm.setNom(nom);
            pharm.setPrenom(prenom);
            pharm.setPassword(passwordEncoder.encode(password));
            pharm.setNumeroLicence(licence.isEmpty() ? null : licence);

            Pharmacie ph = new Pharmacie();
            ph.setNom(pharmacyName.isEmpty() ? (prenom + " " + nom).trim() : pharmacyName);
            ph.setAdresse(adresse);
            ph.setStatut(StatutPharmacie.EN_ATTENTE);
            ph.setTelephone(pharmacyPhone.isEmpty() ? phone : pharmacyPhone);
            ph.setNumeroLicence(licence.isEmpty() ? null : licence);
            ph.setHoraires(normalizeHoursValue(horaires));
            ph.setEstDeGarde(estDeGarde);
            ph.setContact(contact.isEmpty() ? null : contact);
            if (!latitudeValue.isEmpty()) {
                try {
                    ph.setLatitude(Double.parseDouble(latitudeValue));
                } catch (NumberFormatException ignored) {
                    // keep null if invalid
                }
            }
            if (!longitudeValue.isEmpty()) {
                try {
                    ph.setLongitude(Double.parseDouble(longitudeValue));
                } catch (NumberFormatException ignored) {
                    // keep null if invalid
                }
            }

            List<String> documentUrls = new ArrayList<>();
            if (legalDocs != null) {
                for (MultipartFile file : legalDocs) {
                    if (file != null && !file.isEmpty()) {
                        try {
                            documentUrls.add(storeUploadedFile(file, "documents"));
                        } catch (IOException e) {
                            // Log exception for debugging
                            e.printStackTrace();
                            String hint = "Vérifiez que le fichier est accessible localement (évitez les dossiers OneDrive/Drive synchronisés) et réessayez.";
                            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Échec du stockage d’un document légal. " + hint));
                        }
                    }
                }
            }
            if (!documentUrls.isEmpty()) {
                ph.setDocumentLegalUrl(documentUrls.get(0));
                ph.setFichierUrl(documentUrls.size() > 1 ? documentUrls.get(1) : documentUrls.get(0));
            }

            List<String> imageUrls = new ArrayList<>();
            if (pharmacyImages != null) {
                for (MultipartFile file : pharmacyImages) {
                    if (file != null && !file.isEmpty()) {
                        try {
                            imageUrls.add(storeUploadedFile(file, "images"));
                        } catch (IOException e) {
                            // Log exception for debugging
                            e.printStackTrace();
                            String hint = "Vérifiez que le fichier est accessible localement (évitez les dossiers OneDrive/Drive synchronisés) et réessayez.";
                            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Échec du stockage d’une image. " + hint));
                        }
                    }
                }
            }
            if (!imageUrls.isEmpty()) {
                ph.setPhoto1Url(imageUrls.get(0));
                if (imageUrls.size() > 1) ph.setPhoto2Url(imageUrls.get(1));
                if (imageUrls.size() > 2) ph.setPhoto3Url(imageUrls.get(2));
            }

            pharmacieRepository.save(ph);
            pharm.setPharmacie(ph);
            user = pharm;
        } else {
            Patient p = new Patient();
            p.setEmail(email);
            p.setNom(nom);
            p.setPrenom(prenom);
            p.setPassword(passwordEncoder.encode(password));
            user = p;
        }

        Utilisateur savedUser = utilisateurRepository.save(user);

        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());
        JwtAuthenticationResponse response = new JwtAuthenticationResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());

        Map<String, Object> pharmacyInfo = new java.util.HashMap<>();
        if (savedUser instanceof Pharmacien pharmacist && pharmacist.getPharmacie() != null) {
            Pharmacie pharmacy = pharmacist.getPharmacie();
            pharmacyInfo.put("documentLegalUrl", pharmacy.getDocumentLegalUrl());
            pharmacyInfo.put("photo1Url", pharmacy.getPhoto1Url());
            pharmacyInfo.put("photo2Url", pharmacy.getPhoto2Url());
            pharmacyInfo.put("photo3Url", pharmacy.getPhoto3Url());
        }

        Map<String, Object> responseBody = new java.util.HashMap<>();
        responseBody.put("success", true);
        responseBody.put("message", "User registered successfully");
        responseBody.put("token", response.getToken());
        responseBody.put("type", response.getType());
        responseBody.put("user", Map.of(
            "id", savedUser.getId(),
            "role", savedUser.getRole().name().toLowerCase(),
            "name", buildDisplayName(savedUser.getNom(), savedUser.getPrenom()),
            "email", savedUser.getEmail()
        ));
        if (!pharmacyInfo.isEmpty()) {
            responseBody.put("pharmacy", pharmacyInfo);
        }

        return ResponseEntity.ok(responseBody);
    }

    private String buildDisplayName(String nom, String prenom) {
        String fullName = String.join(" ", java.util.stream.Stream.of(prenom, nom)
            .filter(value -> value != null && !value.isBlank())
            .toList());
        return fullName.isBlank() ? "Utilisateur" : fullName;
    }

    private String normalizeHoursValue(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        String trimmed = rawValue.trim();
        if (trimmed.startsWith("{")) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Map<String, String>> parsed = mapper.readValue(trimmed, new TypeReference<>() {});
                if (parsed != null && !parsed.isEmpty()) {
                    return mapper.writeValueAsString(parsed);
                }
            } catch (Exception ignored) {
                // keep original string if parsing fails
            }
        }

        return trimmed;
    }

    private String storeUploadedFile(MultipartFile file, String folder) throws IOException {
        try {
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
            }
            Path uploadDir = Paths.get("uploads", "pharmacies", folder).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            String storedName = UUID.randomUUID() + extension;
            Path target = uploadDir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/pharmacies/" + folder + "/" + storedName;
        } catch (IOException e) {
            // IO problems propagate as-is
            throw e;
        } catch (Exception e) {
            // Convert any other runtime exception to IOException so caller can handle uniformly
            throw new IOException("Failed to store uploaded file: " + e.getMessage(), e);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Utilisateur user = securityUtil.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Non authentifié"));
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "user", Map.of(
                "id", user.getId(),
                "role", user.getRole().name().toLowerCase(),
                "name", user.getNom(),
                "email", user.getEmail()
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

    // DEV only: reset password for a given admin email (requires dev secret)
    @PostMapping("/admin/reset-password")
    public ResponseEntity<?> resetAdminPassword(@RequestBody Map<String, String> payload) {
        String secret = payload.getOrDefault("secret", "");
        // simple dev-only guard
        if (!"proxymedoc-dev-reset".equals(secret)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Forbidden"));
        }

        String email = payload.getOrDefault("email", "");
        String newPassword = payload.getOrDefault("password", "");
        if (email.isEmpty() || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "email and password required"));
        }

        Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "User not found"));
        }

        Utilisateur user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(user);

        return ResponseEntity.ok(Map.of("success", true, "message", "Password reset"));
    }
}
