package com.proxymedoc.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateCommande;
    private Double montantTotal;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    @Enumerated(EnumType.STRING)
    private TypeCommande typeCommande;

    @ManyToOne
    private Utilisateur patient;

    @ManyToOne
    private Pharmacie pharmacie;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePanier> lignes = new ArrayList<>();

    @ManyToOne
    private Reservation reservation;

    private String motifRefus;

    public Commande() {
        this.dateCommande = LocalDateTime.now();
        this.statut = StatutCommande.EN_ATTENTE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }
    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }
    public TypeCommande getTypeCommande() { return typeCommande; }
    public void setTypeCommande(TypeCommande typeCommande) { this.typeCommande = typeCommande; }
    public Utilisateur getPatient() { return patient; }
    public void setPatient(Utilisateur patient) { this.patient = patient; }
    public Pharmacie getPharmacie() { return pharmacie; }
    public void setPharmacie(Pharmacie pharmacie) { this.pharmacie = pharmacie; }
    public List<LignePanier> getLignes() { return lignes; }
    public void setLignes(List<LignePanier> lignes) { this.lignes = lignes; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public String getMotifRefus() { return motifRefus; }
    public void setMotifRefus(String motifRefus) { this.motifRefus = motifRefus; }

    public void valider() {
        this.statut = StatutCommande.VALIDEE;
    }

    public void annuler() {
        this.statut = StatutCommande.ANNULEE;
    }

    public Double getRecapitulatif() {
        return lignes.stream().mapToDouble(LignePanier::getSousTotal).sum();
    }
}
