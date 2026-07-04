package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Panier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PanierRepository extends JpaRepository<Panier, Long> {
}
