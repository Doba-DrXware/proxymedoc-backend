package com.proxymedoc.backend.model;

import jakarta.persistence.Entity;

@Entity
public class Patient extends Utilisateur {

    private Double latitude;
    private Double longitude;

    public Patient() {
        setRole(Role.PATIENT);
    }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
