package com.proxymedoc.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReservationDTO {
    private Long id;

    @NotNull(message = "L'ID du patient est requis")
    private Long patientId;

    @NotNull(message = "L'ID du médicament est requis")
    private Long medicamentId;

    @NotNull(message = "L'ID de la pharmacie est requis")
    private Long pharmacieId;

    private LocalDate dateExpiration;
    private String statut;
    private String motifRefus;

    public ReservationDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getMedicamentId() { return medicamentId; }
    public void setMedicamentId(Long medicamentId) { this.medicamentId = medicamentId; }
    public Long getPharmacieId() { return pharmacieId; }
    public void setPharmacieId(Long pharmacieId) { this.pharmacieId = pharmacieId; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getMotifRefus() { return motifRefus; }
    public void setMotifRefus(String motifRefus) { this.motifRefus = motifRefus; }
}
