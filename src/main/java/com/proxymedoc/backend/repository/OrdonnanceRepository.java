package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
}
