package com.proxymedoc.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class NotificationDTO {
    private Long id;

    @NotBlank(message = "Le message est requis")
    private String message;

    @NotNull(message = "Le type est requis")
    private String type;

    @NotNull(message = "L'ID du destinataire est requis")
    private Long destinataireId;

    private Boolean estLue;

    public NotificationDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getDestinataire() { return destinataireId; }
    public void setDestinataire(Long destinataireId) { this.destinataireId = destinataireId; }
    public Boolean getEstLue() { return estLue; }
    public void setEstLue(Boolean estLue) { this.estLue = estLue; }
}
