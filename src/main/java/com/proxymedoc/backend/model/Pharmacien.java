package com.proxymedoc.backend.model;

import jakarta.persistence.Entity;

@Entity
public class Pharmacien extends Utilisateur {

    private String numeroLicence;
    private Boolean estActif;

    public Pharmacien() {
        setRole(Role.PHARMACIE);
    }

    public String getNumeroLicence() { return numeroLicence; }
    public void setNumeroLicence(String numeroLicence) { this.numeroLicence = numeroLicence; }
    public Boolean getEstActif() { return estActif; }
    public void setEstActif(Boolean estActif) { this.estActif = estActif; }
}
