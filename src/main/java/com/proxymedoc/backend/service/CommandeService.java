package com.proxymedoc.backend.service;

import com.proxymedoc.backend.model.*;
import com.proxymedoc.backend.repository.CommandeRepository;
import com.proxymedoc.backend.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final StockRepository stockRepository;

    public CommandeService(CommandeRepository commandeRepository, StockRepository stockRepository) {
        this.commandeRepository = commandeRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public Commande create(Commande cmd) {
        // calculate total
        double total = cmd.getLignes().stream().mapToDouble(LignePanier::getSousTotal).sum();
        cmd.setMontantTotal(total);
        cmd.setStatut(StatutCommande.EN_ATTENTE);

        Commande saved = commandeRepository.save(cmd);

        // decrement stock where possible
        for (LignePanier lp : saved.getLignes()) {
            if (lp.getMedicament() != null && lp.getMedicament().getId() != null) {
                Optional<Stock> s = stockRepository.findAll().stream()
                        .filter(st -> st.getMedicament().getId().equals(lp.getMedicament().getId()) && st.getPharmacie().getId().equals(saved.getPharmacie().getId()))
                        .findFirst();
                s.ifPresent(stock -> {
                    int remaining = (stock.getQuantiteDisponible() == null ? 0 : stock.getQuantiteDisponible()) - (lp.getQuantite() == null ? 0 : lp.getQuantite());
                    stock.setQuantiteDisponible(Math.max(0, remaining));
                    stockRepository.save(stock);
                });
            }
        }

        return saved;
    }
}
