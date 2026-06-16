package com.capstone.serviceplatform.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class ReservationRequest {
    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;

    @NotNull(message = "L'ID du prestataire est obligatoire")
    private Long prestataireId;

    @NotNull(message = "L'ID du service est obligatoire")
    private Long serviceId;

    @NotNull(message = "La date/heure est obligatoire")
    @Future(message = "La date doit être dans le futur")
    private LocalDateTime dateHeure;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @Positive(message = "Le montant doit être supérieur à 0")
    private Double montant;

    // getters et setters
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getPrestataireId() {
        return prestataireId;
    }

    public void setPrestataireId(Long prestataireId) {
        this.prestataireId = prestataireId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}