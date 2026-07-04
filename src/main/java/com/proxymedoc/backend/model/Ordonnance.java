package com.proxymedoc.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Ordonnance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fichierUrl;
    private String documentLegalUrl;
    private String photo1Url;
    private String photo2Url;
    private String photo3Url;
    private String commentairePharmacie;

    @Enumerated(EnumType.STRING)
    private StatutOrdonnance statut;

    @ManyToOne
    private Utilisateur patient;

    @ManyToOne
    private Pharmacie pharmacie;

    public Ordonnance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public StatutOrdonnance getStatut() { return statut; }
    public void setStatut(StatutOrdonnance statut) { this.statut = statut; }
    public Utilisateur getPatient() { return patient; }
    public void setPatient(Utilisateur patient) { this.patient = patient; }
    public Pharmacie getPharmacie() { return pharmacie; }
    public void setPharmacie(Pharmacie pharmacie) { this.pharmacie = pharmacie; }

    public boolean estValide() {
        return statut == StatutOrdonnance.VALIDEE;
    }
}
