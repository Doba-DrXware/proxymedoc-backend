package com.proxymedoc.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class LignePanierDTO {
    private Long id;

    @NotNull(message = "La quantité est requise")
    @Min(value = 1, message = "La quantité doit être >= 1")
    private Integer quantite;

    @NotNull(message = "Le prix unitaire est requis")
    @Min(value = 0, message = "Le prix doit être >= 0")
    private Double prixUnitaire;

    @NotNull(message = "L'ID du médicament est requis")
    private Long medicamentId;

    public LignePanierDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public Long getMedicamentId() { return medicamentId; }
    public void setMedicamentId(Long medicamentId) { this.medicamentId = medicamentId; }
}
