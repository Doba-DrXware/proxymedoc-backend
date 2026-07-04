package com.proxymedoc.backend.service;

import com.proxymedoc.backend.model.Notification;
import com.proxymedoc.backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification create(Notification n) {
        return notificationRepository.save(n);
    }

    public List<Notification> forUser(Long userId) {
        return notificationRepository.findByDestinataireId(userId);
    }
}
