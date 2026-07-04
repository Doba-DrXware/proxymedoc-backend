package com.proxymedoc.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class MedicamentDTO {
    private Long id;

    @NotBlank(message = "Le nom du médicament est requis")
    private String nom;

    private String denomination;
    private String categorie;
    private String description;

    @NotNull(message = "Le prix est requis")
    @Min(value = 0, message = "Le prix doit être >= 0")
    private Double prixUnitaire;

    private String formeGalenique;
    private String dosage;
    private Boolean exigeOrdonnance;
    private String imageUrl;
    private String noticeUrl;

    public MedicamentDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDenomination() { return denomination; }
    public void setDenomination(String denomination) { this.denomination = denomination; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public String getFormeGalenique() { return formeGalenique; }
    public void setFormeGalenique(String formeGalenique) { this.formeGalenique = formeGalenique; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public Boolean getExigeOrdonnance() { return exigeOrdonnance; }
    public void setExigeOrdonnance(Boolean exigeOrdonnance) { this.exigeOrdonnance = exigeOrdonnance; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getNoticeUrl() { return noticeUrl; }
    public void setNoticeUrl(String noticeUrl) { this.noticeUrl = noticeUrl; }
}
