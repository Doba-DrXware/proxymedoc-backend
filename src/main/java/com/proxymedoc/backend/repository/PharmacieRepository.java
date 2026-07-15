package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Pharmacie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacieRepository extends JpaRepository<Pharmacie, Long> {
    Optional<Pharmacie> findByNomIgnoreCase(String nom);
}
