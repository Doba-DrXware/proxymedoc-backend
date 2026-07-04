package com.proxymedoc.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CommandeDTO {
    private Long id;

    @NotNull(message = "L'ID du patient est requis")
    private Long patientId;

    @NotNull(message = "L'ID de la pharmacie est requis")
    private Long pharmacieId;

    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    private List<LignePanierDTO> lignes;

    private String typeCommande;
    private String statut;

    public CommandeDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPharmacieId() { return pharmacieId; }
    public void setPharmacieId(Long pharmacieId) { this.pharmacieId = pharmacieId; }
    public List<LignePanierDTO> getLignes() { return lignes; }
    public void setLignes(List<LignePanierDTO> lignes) { this.lignes = lignes; }
    public String getTypeCommande() { return typeCommande; }
    public void setTypeCommande(String typeCommande) { this.typeCommande = typeCommande; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
