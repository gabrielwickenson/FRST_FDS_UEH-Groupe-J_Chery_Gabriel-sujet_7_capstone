package com.capstone.serviceplatform.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prestataire")
@PrimaryKeyJoinColumn(name = "id")
public class Prestataire extends User {
    private String competences;
    private BigDecimal tarifHoraire;
    private String zoneIntervention;
    private BigDecimal moyenneNotes;

    // getters et setters
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