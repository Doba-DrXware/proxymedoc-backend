package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Pharmacie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacieRepository extends JpaRepository<Pharmacie, Long> {
}
