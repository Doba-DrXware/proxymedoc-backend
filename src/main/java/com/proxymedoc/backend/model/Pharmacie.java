package com.proxymedoc.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pharmacie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String nom;

    @Column(length = 1000)
    private String adresse;

    private Double latitude;
    private Double longitude;

    @Column(length = 50)
    private String telephone;

    @Enumerated(EnumType.STRING)
    private StatutPharmacie statut;

    @Column(columnDefinition = "TEXT")
    private String horaires;

    private Boolean estDeGarde;

    @Column(length = 200)
    private String numeroLicence;

    

    @Column(length = 1000)
    private String photo1Url;

    @Column(length = 1000)
    private String photo2Url;

    @Column(length = 1000)
    private String photo3Url;

    @Column(length = 1000, name = "fichier_rc")
    private String fichierRc;

    @Column(length = 1000, name = "agrement_minsante")
    private String agrementMinsante;

    @Column(columnDefinition = "TEXT")
    private String commentairePharmacie;

    private Integer scoreIa;

    @Column(length = 500)
    private String contact;

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commande> commandes = new ArrayList<>();

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ordonnance> ordonnances = new ArrayList<>();

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    public Pharmacie() {}

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
    public StatutPharmacie getStatut() { return statut; }
    public void setStatut(StatutPharmacie statut) { this.statut = statut; }
    public String getHoraires() { return horaires; }
    public void setHoraires(String horaires) { this.horaires = horaires; }
    public Boolean getEstDeGarde() { return estDeGarde; }
    public void setEstDeGarde(Boolean estDeGarde) { this.estDeGarde = estDeGarde; }
    public String getNumeroLicence() { return numeroLicence; }
    public void setNumeroLicence(String numeroLicence) { this.numeroLicence = numeroLicence; }
    
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
    public String getCommentairePharmacie() { return commentairePharmacie; }
    public void setCommentairePharmacie(String commentairePharmacie) { this.commentairePharmacie = commentairePharmacie; }
    public Integer getScoreIa() { return scoreIa; }
    public void setScoreIa(Integer scoreIa) { this.scoreIa = scoreIa; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public List<Stock> getStocks() { return stocks; }
    public void setStocks(List<Stock> stocks) { this.stocks = stocks; }
    public List<Commande> getCommandes() { return commandes; }
    public void setCommandes(List<Commande> commandes) { this.commandes = commandes; }
    public List<Ordonnance> getOrdonnances() { return ordonnances; }
    public void setOrdonnances(List<Ordonnance> ordonnances) { this.ordonnances = ordonnances; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public List<Medicament> getMedicamentsDisponibles() {
        return stocks.stream().map(Stock::getMedicament).toList();
    }

    public void mettreAJourStock() {
        stocks.forEach(stock -> stock.setDateMAJ(java.time.LocalDate.now()));
    }

    public void ajouterMedicament(Medicament medicament, Integer quantite, Double seuilAlerte) {
        if (medicament != null) {
            Stock stock = new Stock();
            stock.setMedicament(medicament);
            stock.setPharmacie(this);
            stock.setQuantiteDisponible(quantite);
            stock.setSeuilAlerte(seuilAlerte != null ? seuilAlerte.intValue() : 0);
            stock.setDateMAJ(java.time.LocalDate.now());
            stocks.add(stock);
        }
    }

    public void modifierMedicament(Medicament medicament) {
        medicament.getStocks().stream().filter(stock -> stock.getPharmacie().equals(this)).findFirst().ifPresent(stock -> stock.setDateMAJ(java.time.LocalDate.now()));
    }
}
