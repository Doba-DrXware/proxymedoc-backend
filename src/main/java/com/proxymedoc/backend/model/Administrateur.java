package com.proxymedoc.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrateurs")
public class Administrateur extends Utilisateur {

    private String niveauAcces;

    public Administrateur() {
        setRole(Role.ADMIN);
    }

    public String getNiveauAcces() { return niveauAcces; }
    public void setNiveauAcces(String niveauAcces) { this.niveauAcces = niveauAcces; }

    public void validerPharmacie(Pharmacie ph) {
        ph.setStatut(StatutPharmacie.VALIDEE);
    }

    public void suspendrePharmacie(Pharmacie ph) {
        ph.setStatut(StatutPharmacie.SUSPENDUE);
    }
}
