package com.proxymedoc.backend.model;

import jakarta.persistence.*;

@Entity
public class CommandePersonnalisee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomMedicament;
    private String description;
    private Integer quantite;

    @Enumerated(EnumType.STRING)
    private StatutCommande statutCommande;

    @ManyToOne
    private Utilisateur patient;

    public CommandePersonnalisee() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomMedicament() { return nomMedicament; }
    public void setNomMedicament(String nomMedicament) { this.nomMedicament = nomMedicament; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public StatutCommande getStatutCommande() { return statutCommande; }
    public void setStatutCommande(StatutCommande statutCommande) { this.statutCommande = statutCommande; }
    public Utilisateur getPatient() { return patient; }
    public void setPatient(Utilisateur patient) { this.patient = patient; }

    public void notifierPharmacies() {
        // TODO: implémenter la notification des pharmacies
    }
}
