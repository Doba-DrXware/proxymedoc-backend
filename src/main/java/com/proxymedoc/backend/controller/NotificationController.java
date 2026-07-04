package com.proxymedoc.backend.controller;

import com.proxymedoc.backend.dto.NotificationDTO;
import com.proxymedoc.backend.model.Notification;
import com.proxymedoc.backend.model.TypeNotification;
import com.proxymedoc.backend.model.Utilisateur;
import com.proxymedoc.backend.repository.NotificationRepository;
import com.proxymedoc.backend.repository.UtilisateurRepository;
import com.proxymedoc.backend.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final NotificationService notificationService;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationController(NotificationService notificationService, UtilisateurRepository utilisateurRepository) {
        this.notificationService = notificationService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> forUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.forUser(userId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> create(@Valid @RequestBody NotificationDTO dto) {
        Utilisateur destinataire = utilisateurRepository.findById(dto.getDestinataire()).orElse(null);

        if (destinataire == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        Notification notif = new Notification();
        notif.setMessage(dto.getMessage());
        notif.setType(TypeNotification.valueOf(dto.getType()));
        notif.setDestinataire(destinataire);
        notif.setDateEnvoi(LocalDateTime.now());
        notif.setEstLue(false);

        Notification saved = notificationService.create(notif);
        return ResponseEntity.ok(Map.of("id", saved.getId(), "type", saved.getType()));
    }
}
