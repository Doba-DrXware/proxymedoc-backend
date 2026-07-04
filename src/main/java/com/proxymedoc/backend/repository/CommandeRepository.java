package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
