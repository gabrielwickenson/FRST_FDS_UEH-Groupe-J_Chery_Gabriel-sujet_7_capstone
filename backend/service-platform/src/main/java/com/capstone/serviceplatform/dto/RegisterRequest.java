package com.capstone.serviceplatform.dto;

import com.capstone.serviceplatform.entity.Role;

public class RegisterRequest {
    private String nom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String photo;
    private Role role;
    // champs spécifiques client
    private String adresseParDefaut;
    // champs spécifiques prestataire
    private String competences;
    private Double tarifHoraire;
    private String zoneIntervention;

    // Getters et setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAdresseParDefaut() {
        return adresseParDefaut;
    }

    public void setAdresseParDefaut(String adresseParDefaut) {
        this.adresseParDefaut = adresseParDefaut;
    }

    public String getCompetences() {
        return competences;
    }

    public void setCompetences(String competences) {
        this.competences = competences;
    }

    public Double getTarifHoraire() {
        return tarifHoraire;
    }

    public void setTarifHoraire(Double tarifHoraire) {
        this.tarifHoraire = tarifHoraire;
    }

    public String getZoneIntervention() {
        return zoneIntervention;
    }

    public void setZoneIntervention(String zoneIntervention) {
        this.zoneIntervention = zoneIntervention;
    }
}