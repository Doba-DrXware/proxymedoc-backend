package com.proxymedoc.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Medicament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String denomination;
    private String categorie;

    @Column(length = 2000)
    private String description;

    private Double prixUnitaire;
    private String formeGalenique;
    private String dosage;
    private Boolean exigeOrdonnance;
    private String imageUrl;
    private String noticeUrl;

    @OneToMany(mappedBy = "medicament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks = new ArrayList<>();

    public Medicament() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public List<Stock> getStocks() { return stocks; }
    public void setStocks(List<Stock> stocks) { this.stocks = stocks; }

    public String getDetails() {
        return description;
    }
}
