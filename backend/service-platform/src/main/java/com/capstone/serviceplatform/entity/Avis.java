package com.capstone.serviceplatform.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "avis")
public class Avis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optionnel désormais : un avis peut être laissé directement sur le
    // profil d'un prestataire, sans être rattaché à une réservation précise
    // (ex. avis "libre" depuis la page Profil). Quand l'avis découle d'une
    // réservation terminée (flux historique), ce champ reste renseigné.
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = true)
    private Reservation reservation;

    // Lien direct vers le prestataire évalué. Toujours renseigné (y compris
    // pour les avis issus d'une réservation, où il est recopié depuis
    // reservation.prestataire) afin de pouvoir retrouver tous les avis d'un
    // prestataire sans dépendre de la présence d'une réservation.
    // Typé User (voir Reservation.java) : un compte présent dans les deux
    // tables filles (client + prestataire) est matérialisé comme Client par
    // Hibernate, ce qui cassait un champ typé Prestataire.
    @ManyToOne
    @JoinColumn(name = "prestataire_id", nullable = true)
    private User prestataire;

    // Auteur de l'avis (le client, ou un compte PRESTATAIRE agissant comme
    // client).
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private User client;

    private Integer note;
    private String commentaire;
    private Date date;

    // getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public User getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(User prestataire) {
        this.prestataire = prestataire;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}