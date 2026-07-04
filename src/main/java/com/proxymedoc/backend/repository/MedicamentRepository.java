package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
}
