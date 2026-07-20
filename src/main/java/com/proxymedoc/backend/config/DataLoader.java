package com.proxymedoc.backend.config;

import com.proxymedoc.backend.model.*;
import com.proxymedoc.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final PharmacieRepository pharmacieRepository;
    private final MedicamentRepository medicamentRepository;
    private final StockRepository stockRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(PharmacieRepository pharmacieRepository, MedicamentRepository medicamentRepository, StockRepository stockRepository, UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.pharmacieRepository = pharmacieRepository;
        this.medicamentRepository = medicamentRepository;
        this.stockRepository = stockRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        ensureAdmin();

        Medicament amox250 = ensureMedicament(
                "Amoxicilline",
                "antibiotique",
                "Antibiotique standard.",
                "comprime",
                "250mg",
                false
        );

        Medicament parac = ensureMedicament(
                "Paracétamol",
                "analgesique",
                "Analgesique courant.",
                "comprime",
                "500mg",
                false
        );

        Medicament ibup = ensureMedicament(
                "Ibuprofène",
                "anti-inflammatoire",
                "Anti-inflammatoire et antalgique.",
                "comprime",
                "400mg",
                false
        );

        Medicament vitamineC = ensureMedicament(
                "Vitamine C",
                "vitamine",
                "Complément nutritionnel.",
                "gélule",
                "1000mg",
                false
        );

        Medicament omeprazole = ensureMedicament(
                "Oméprazole",
                "gastro-entérologique",
                "Traitement contre les brûlures d’estomac.",
                "comprime",
                "20mg",
                false
        );

        Pharmacie palais = ensurePharmacie(
                "Pharmacie du Palais",
                "Bastos, Yaoundé",
                3.8667,
                11.5167,
                "+237 699 123 456",
                "Lun-Sam: 07h-22h",
                true
        );

        Pharmacie centrale = ensurePharmacie(
                "Pharmacie Centrale",
                "Centre-ville, Yaoundé",
                3.8480,
                11.5021,
                "+237 677 654 321",
                "Lun-Sam: 07h-23h",
                true
        );

        Pharmacie concorde = ensurePharmacie(
                "Pharmacie de la Concorde",
                "Mokolo, Yaoundé",
                3.8765,
                11.5098,
                "+237 690 456 789",
                "Lun-Sam: 08h-21h",
                false
        );

        Pharmacie nation = ensurePharmacie(
                "Pharmacie des Nations",
                "Nkolbisson, Yaoundé",
                3.9001,
                11.4932,
                "+237 655 112 233",
                "Lun-Sam: 06h-22h",
                true
        );

        ensureStock(amox250, palais, 30, 900.0, 5);
        ensureStock(parac, palais, 100, 250.0, 10);
        ensureStock(ibup, palais, 40, 1800.0, 6);
        ensureStock(vitamineC, palais, 25, 3000.0, 8);

        ensureStock(amox250, centrale, 45, 1050.0, 5);
        ensureStock(parac, centrale, 90, 280.0, 10);
        ensureStock(ibup, centrale, 35, 1900.0, 6);
        ensureStock(omeprazole, centrale, 20, 2200.0, 4);

        ensureStock(amox250, concorde, 35, 950.0, 5);
        ensureStock(parac, concorde, 110, 270.0, 10);
        ensureStock(vitamineC, concorde, 30, 3100.0, 8);
        ensureStock(omeprazole, concorde, 25, 2400.0, 4);

        ensureStock(amox250, nation, 50, 1100.0, 5);
        ensureStock(parac, nation, 80, 300.0, 10);
        ensureStock(ibup, nation, 30, 2000.0, 6);
        ensureStock(vitamineC, nation, 28, 3200.0, 8);
        ensureStock(omeprazole, nation, 22, 2600.0, 4);
    }

    private void ensureAdmin() {
        if (utilisateurRepository.findByEmail("admin@proxymedoc.com").isEmpty()) {
            Administrateur admin = new Administrateur();
            admin.setEmail("admin@proxymedoc.com");
            admin.setPassword(passwordEncoder.encode("proxyadmin"));
            admin.setNom("Admin");
            admin.setPrenom("ProxyMedoc");
            utilisateurRepository.save(admin);
        }
    }

    private Medicament ensureMedicament(String denomination, String categorie, String description, String formeGalenique, String dosage, Boolean exigeOrdonnance) {
        return medicamentRepository.findByDenominationIgnoreCase(denomination)
                .orElseGet(() -> {
                    Medicament medicament = new Medicament();
                    medicament.setDenomination(denomination);
                    medicament.setCategorie(categorie);
                    medicament.setDescription(description);
                    medicament.setFormeGalenique(formeGalenique);
                    medicament.setDosage(dosage);
                    medicament.setExigeOrdonnance(exigeOrdonnance);
                    return medicamentRepository.save(medicament);
                });
    }

    private Pharmacie ensurePharmacie(String nom, String adresse, Double latitude, Double longitude, String telephone, String horaires, Boolean estDeGarde) {
        return pharmacieRepository.findByNomIgnoreCase(nom)
                .orElseGet(() -> {
                    Pharmacie pharmacie = new Pharmacie();
                    pharmacie.setNom(nom);
                    pharmacie.setAdresse(adresse);
                    pharmacie.setLatitude(latitude);
                    pharmacie.setLongitude(longitude);
                    pharmacie.setTelephone(telephone);
                    pharmacie.setHoraires(horaires);
                    pharmacie.setEstDeGarde(estDeGarde);
                    pharmacie.setStatut(StatutPharmacie.VALIDEE);
                    return pharmacieRepository.save(pharmacie);
                });
    }

    private void ensureStock(Medicament medicament, Pharmacie pharmacie, Integer quantiteDisponible, Double prixUnitaire, Integer seuilAlerte) {
        Stock stock = stockRepository.findByMedicamentIdAndPharmacieId(medicament.getId(), pharmacie.getId())
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setMedicament(medicament);
                    newStock.setPharmacie(pharmacie);
                    return newStock;
                });

        stock.setQuantiteDisponible(quantiteDisponible);
        stock.setPrixUnitaire(prixUnitaire);
        stock.setSeuilAlerte(seuilAlerte);
        stock.setDateMAJ(LocalDate.now());
        stockRepository.save(stock);
    }
}
