package com.proxymedoc.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class OrdonnanceDTO {
    private Long id;

    @NotNull(message = "L'ID du patient est requis")
    private Long patientId;

    @NotNull(message = "L'ID de la pharmacie est requis")
    private Long pharmacieId;

    private String fichierUrl;
    private String documentLegalUrl;
    private String photo1Url;
    private String photo2Url;
    private String photo3Url;
    private String commentairePharmacie;
    private String statut;

    public OrdonnanceDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPharmacieId() { return pharmacieId; }
    public void setPharmacieId(Long pharmacieId) { this.pharmacieId = pharmacieId; }
    public String getFichierUrl() { return fichierUrl; }
    public void setFichierUrl(String fichierUrl) { this.fichierUrl = fichierUrl; }
    public String getDocumentLegalUrl() { return documentLegalUrl; }
    public void setDocumentLegalUrl(String documentLegalUrl) { this.documentLegalUrl = documentLegalUrl; }
    public String getPhoto1Url() { return photo1Url; }
    public void setPhoto1Url(String photo1Url) { this.photo1Url = photo1Url; }
    public String getPhoto2Url() { return photo2Url; }
    public void setPhoto2Url(String photo2Url) { this.photo2Url = photo2Url; }
    public String getPhoto3Url() { return photo3Url; }
    public void setPhoto3Url(String photo3Url) { this.photo3Url = photo3Url; }
    public String getCommentairePharmacie() { return commentairePharmacie; }
    public void setCommentairePharmacie(String commentairePharmacie) { this.commentairePharmacie = commentairePharmacie; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
