package com.proxymedoc.backend.config;

import com.proxymedoc.backend.model.*;
import com.proxymedoc.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final PharmacieRepository pharmacieRepository;
    private final MedicamentRepository medicamentRepository;
    private final StockRepository stockRepository;

    public DataLoader(PharmacieRepository pharmacieRepository, MedicamentRepository medicamentRepository, StockRepository stockRepository) {
        this.pharmacieRepository = pharmacieRepository;
        this.medicamentRepository = medicamentRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (pharmacieRepository.count() > 0) return; // don't seed twice

        Medicament amox250 = new Medicament();
        amox250.setNom("Amoxicilline 250mg");
        amox250.setDenomination("Amoxicilline");
        amox250.setCategorie("antibiotique");
        amox250.setPrixUnitaire(900.0);
        amox250.setDescription("Antibiotique standard.");
        medicamentRepository.save(amox250);

        Medicament parac = new Medicament();
        parac.setNom("Paracétamol 500mg");
        parac.setDenomination("Paracétamol");
        parac.setCategorie("analgesique");
        parac.setPrixUnitaire(250.0);
        parac.setDescription("Analgesique.");
        medicamentRepository.save(parac);

        Pharmacie p1 = new Pharmacie();
        p1.setNom("Pharmacie du Palais");
        p1.setAdresse("Bastos, Yaoundé");
        p1.setLatitude(3.8667);
        p1.setLongitude(11.5167);
        p1.setTelephone("+237 699 123 456");
        p1.setStatut(StatutPharmacie.VALIDEE);
        pharmacieRepository.save(p1);

        Stock s1 = new Stock();
        s1.setMedicament(amox250);
        s1.setPharmacie(p1);
        s1.setQuantiteDisponible(30);
        s1.setSeuilAlerte(5);
        stockRepository.save(s1);

        Stock s2 = new Stock();
        s2.setMedicament(parac);
        s2.setPharmacie(p1);
        s2.setQuantiteDisponible(100);
        s2.setSeuilAlerte(10);
        stockRepository.save(s2);

        Pharmacie p2 = new Pharmacie();
        p2.setNom("Pharmacie Centrale");
        p2.setAdresse("Centre-ville, Yaoundé");
        p2.setLatitude(3.8480);
        p2.setLongitude(11.5021);
        p2.setTelephone("+237 677 654 321");
        p2.setStatut(StatutPharmacie.VALIDEE);
        pharmacieRepository.save(p2);

        Stock s3 = new Stock();
        s3.setMedicament(amox250);
        s3.setPharmacie(p2);
        s3.setQuantiteDisponible(70);
        s3.setSeuilAlerte(10);
        stockRepository.save(s3);

    }
}
