package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
