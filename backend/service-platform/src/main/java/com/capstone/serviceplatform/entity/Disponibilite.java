package com.capstone.serviceplatform.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilite")
public class Disponibilite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prestataire_id", nullable = false)
    private Prestataire prestataire;

    private String jour; // LUNDI, MARDI, ...
    private LocalTime heureDebut;
    private LocalTime heureFin;
     // get set
    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Prestataire getPrestataire() { return prestataire; }
    public void setPrestataire(Prestataire prestataire) { this.prestataire = prestataire; }
    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
}