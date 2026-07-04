package com.proxymedoc.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class StockDTO {
    private Long id;

    @NotNull(message = "La quantité est requise")
    @Min(value = 0, message = "La quantité doit être >= 0")
    private Integer quantiteDisponible;

    @NotNull(message = "Le seuil d'alerte est requis")
    @Min(value = 0, message = "Le seuil doit être >= 0")
    private Integer seuilAlerte;

    @NotNull(message = "L'ID du médicament est requis")
    private Long medicamentId;

    @NotNull(message = "L'ID de la pharmacie est requis")
    private Long pharmacieId;

    public StockDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(Integer quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }
    public Integer getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(Integer seuilAlerte) { this.seuilAlerte = seuilAlerte; }
    public Long getMedicamentId() { return medicamentId; }
    public void setMedicamentId(Long medicamentId) { this.medicamentId = medicamentId; }
    public Long getPharmacieId() { return pharmacieId; }
    public void setPharmacieId(Long pharmacieId) { this.pharmacieId = pharmacieId; }
}
