package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByMedicamentIdAndPharmacieId(Long medicamentId, Long pharmacieId);
}
