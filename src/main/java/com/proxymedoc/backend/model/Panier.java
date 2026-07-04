package com.proxymedoc.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    private StatutPanier statut;

    @ManyToOne
    private Utilisateur patient;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePanier> lignes = new ArrayList<>();

    public Panier() {
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutPanier.EN_ATTENTE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public StatutPanier getStatut() { return statut; }
    public void setStatut(StatutPanier statut) { this.statut = statut; }
    public Utilisateur getPatient() { return patient; }
    public void setPatient(Utilisateur patient) { this.patient = patient; }
    public List<LignePanier> getLignes() { return lignes; }
    public void setLignes(List<LignePanier> lignes) { this.lignes = lignes; }

    public Double calculerTotal() {
        return lignes.stream().mapToDouble(LignePanier::getSousTotal).sum();
    }

    public void vider() {
        lignes.clear();
    }
}
