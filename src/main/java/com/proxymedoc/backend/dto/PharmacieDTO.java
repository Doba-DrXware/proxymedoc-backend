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
    private Integer scoreIa;
    private String contact;
    private String photo1Url;
    private String photo2Url;
    private String photo3Url;
    private String fichierRc;
    private String agrementMinsante;

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
    
    public Integer getScoreIa() { return scoreIa; }
    public void setScoreIa(Integer scoreIa) { this.scoreIa = scoreIa; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getPhoto1Url() { return photo1Url; }
    public void setPhoto1Url(String photo1Url) { this.photo1Url = photo1Url; }
    public String getPhoto2Url() { return photo2Url; }
    public void setPhoto2Url(String photo2Url) { this.photo2Url = photo2Url; }
    public String getPhoto3Url() { return photo3Url; }
    public void setPhoto3Url(String photo3Url) { this.photo3Url = photo3Url; }
    public String getFichierRc() { return fichierRc; }
    public void setFichierRc(String fichierRc) { this.fichierRc = fichierRc; }
    public String getAgrementMinsante() { return agrementMinsante; }
    public void setAgrementMinsante(String agrementMinsante) { this.agrementMinsante = agrementMinsante; }
}
