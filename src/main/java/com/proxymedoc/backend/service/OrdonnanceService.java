package com.proxymedoc.backend.service;

import com.proxymedoc.backend.model.Ordonnance;
import com.proxymedoc.backend.model.StatutOrdonnance;
import com.proxymedoc.backend.repository.OrdonnanceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrdonnanceService {
    private final OrdonnanceRepository ordonnanceRepository;

    public OrdonnanceService(OrdonnanceRepository ordonnanceRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
    }

    public Ordonnance valider(Long id) {
        Optional<Ordonnance> o = ordonnanceRepository.findById(id);
        if (o.isPresent()) {
            Ordonnance ord = o.get();
            ord.setStatut(StatutOrdonnance.VALIDEE);
            return ordonnanceRepository.save(ord);
        }
        return null;
    }
}
