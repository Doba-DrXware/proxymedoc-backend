package com.proxymedoc.backend.dto;

/**
 * DTO simplifié pour les résultats de recherche de médicaments.
 * Contient uniquement les champs nécessaires au patient pour identifier et sélectionner un médicament.
 */
public class MedicamentSearchDTO {
    private Long id;
    private String denomination;
    private String categorie;
    private String dosage;
    private String formeGalenique;

    public MedicamentSearchDTO() {}

    public MedicamentSearchDTO(Long id, String denomination, String categorie, String dosage, String formeGalenique) {
        this.id = id;
        this.denomination = denomination;
        this.categorie = categorie;
        this.dosage = dosage;
        this.formeGalenique = formeGalenique;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFormeGalenique() {
        return formeGalenique;
    }

    public void setFormeGalenique(String formeGalenique) {
        this.formeGalenique = formeGalenique;
    }

    @Override
    public String toString() {
        return "MedicamentSearchDTO{" +
                "id=" + id +
                ", denomination='" + denomination + '\'' +
                ", categorie='" + categorie + '\'' +
                ", dosage='" + dosage + '\'' +
                ", formeGalenique='" + formeGalenique + '\'' +
                '}';
    }
}
