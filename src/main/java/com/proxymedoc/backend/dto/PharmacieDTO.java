package com.proxymedoc.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;

public class PharmacieDTO {
    private Long id;

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "L'adresse est requise")
    private String adresse;

    @NotNull(message = "La latitude est requise")
    private Double latitude;

    @NotNull(message = "La longitude est requise")
    private Double longitude;

    @NotBlank(message = "Le téléphone est requis")
    private String telephone;

    private String statut;
    private String horaires;
    private Boolean estDeGarde;
    private String numeroLicence;
    private Boolean estActif;
    private Integer scoreIa;
    private String contact;

    public PharmacieDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getHoraires() { return horaires; }
    public void setHoraires(String horaires) { this.horaires = horaires; }
    public Boolean getEstDeGarde() { return estDeGarde; }
    public void setEstDeGarde(Boolean estDeGarde) { this.estDeGarde = estDeGarde; }
    public String getNumeroLicence() { return numeroLicence; }
    public void setNumeroLicence(String numeroLicence) { this.numeroLicence = numeroLicence; }
    public Boolean getEstActif() { return estActif; }
    public void setEstActif(Boolean estActif) { this.estActif = estActif; }
    public Integer getScoreIa() { return scoreIa; }
    public void setScoreIa(Integer scoreIa) { this.scoreIa = scoreIa; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
