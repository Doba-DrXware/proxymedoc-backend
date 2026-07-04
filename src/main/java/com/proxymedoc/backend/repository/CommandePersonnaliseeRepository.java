package com.proxymedoc.backend.repository;

import com.proxymedoc.backend.model.CommandePersonnalisee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandePersonnaliseeRepository extends JpaRepository<CommandePersonnalisee, Long> {
}
