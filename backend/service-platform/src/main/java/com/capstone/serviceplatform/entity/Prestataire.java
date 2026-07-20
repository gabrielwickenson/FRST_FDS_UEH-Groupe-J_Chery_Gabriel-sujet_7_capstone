package com.capstone.serviceplatform.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;

@Entity
@Table(name = "prestataire")
@PrimaryKeyJoinColumn(name = "id")
public class Prestataire extends User {
    private String competences;
    private BigDecimal tarifHoraire;
    private String zoneIntervention;
    private BigDecimal moyenneNotes;
    @Formula("(SELECT COUNT(*) FROM avis a JOIN reservation r ON a.reservation_id = r.id WHERE r.prestataire_id = id)")
    private int nombreAvis;
    private Boolean disponible = true;

    // getters et setters
    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public int getNombreAvis() {
        return nombreAvis;
    }

    public void setNombreAvis(int nombreAvis) {
        this.nombreAvis = nombreAvis;
    }

    public String getCompetences() {
        return competences;
    }

    public void setCompetences(String competences) {
        this.competences = competences;
    }

    public BigDecimal getTarifHoraire() {
        return tarifHoraire;
    }

    public void setTarifHoraire(BigDecimal tarifHoraire) {
        this.tarifHoraire = tarifHoraire;
    }

    public String getZoneIntervention() {
        return zoneIntervention;
    }

    public void setZoneIntervention(String zoneIntervention) {
        this.zoneIntervention = zoneIntervention;
    }

    public BigDecimal getMoyenneNotes() {
        return moyenneNotes;
    }

    public void setMoyenneNotes(BigDecimal moyenneNotes) {
        this.moyenneNotes = moyenneNotes;
    }
}