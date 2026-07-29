package com.capstone.serviceplatform.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Références typées User (classe mère) et non Client/Prestataire : avec
    // l'héritage JOINED, un compte peut avoir une ligne dans LES DEUX tables
    // filles (un pro qui réserve aussi en tant que client). Hibernate
    // matérialise alors ce compte comme Client, et un champ typé Prestataire
    // provoquait "Impossible de définir la valeur de type [...Client] :
    // 'Reservation.prestataire' (setter)" — cassant toutes les requêtes de
    // réservations côté pro. Typé User, n'importe quel sous-type convient.
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "prestataire_id", nullable = false)
    private User prestataire;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    private LocalDateTime dateHeure;
    private String adresse;
    private String statut;
    private BigDecimal montant;
    // Date effective du paiement (renseignée par POST /{id}/paiement). La
    // dateHeure est la date de la PRESTATION (souvent future) : les revenus
    // "des 7 derniers jours" doivent se baser sur le moment où l'argent est
    // encaissé, pas sur la date du rendez-vous.
    private LocalDateTime datePaiement;

    // getters et setters (générez-les)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public User getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(User prestataire) {
        this.prestataire = prestataire;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }
}