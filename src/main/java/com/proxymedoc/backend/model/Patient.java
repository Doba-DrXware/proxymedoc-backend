package com.proxymedoc.backend.model;

import jakarta.persistence.Entity;

@Entity
public class Patient extends Utilisateur {

    public Patient() {
        setRole(Role.PATIENT);
    }

}
