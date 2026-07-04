package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireId(Long destinataireId);
}
