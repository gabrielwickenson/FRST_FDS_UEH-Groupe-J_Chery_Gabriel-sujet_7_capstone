package com.capstone.serviceplatform.dto;

public class UpdateUserRequest {
    private String nom;
    private String telephone;
    private String adresseParDefaut;   // pour Client
    private String competences;        // pour Prestataire
    private Double tarifHoraire;       // pour Prestataire
    private String zoneIntervention;   // pour Prestataire

    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresseParDefaut() { return adresseParDefaut; }
    public void setAdresseParDefaut(String adresseParDefaut) { this.adresseParDefaut = adresseParDefaut; }

    public String getCompetences() { return competences; }
    public void setCompetences(String competences) { this.competences = competences; }

    public Double getTarifHoraire() { return tarifHoraire; }
    public void setTarifHoraire(Double tarifHoraire) { this.tarifHoraire = tarifHoraire; }

    public String getZoneIntervention() { return zoneIntervention; }
    public void setZoneIntervention(String zoneIntervention) { this.zoneIntervention = zoneIntervention; }
}