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
    // Description libre affichée dans la section "À propos" du profil
    // public. Modifiable par le prestataire lui-même depuis Paramètres.
    @Column(length = 2000)
    private String bio;
    // Compte les avis liés directement (colonne avis.prestataire_id, toujours
    // renseignée) plutôt que de passer par la réservation, pour inclure les
    // avis laissés directement sur le profil sans réservation associée.
    @Formula("(SELECT COUNT(*) FROM avis a WHERE a.prestataire_id = id)")
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}