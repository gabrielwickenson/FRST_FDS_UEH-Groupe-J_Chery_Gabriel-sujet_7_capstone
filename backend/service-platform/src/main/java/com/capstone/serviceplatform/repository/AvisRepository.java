package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AvisRepository extends JpaRepository<Avis, Long> {
    // Ancienne requête (avis rattachés à une réservation) — conservée pour
    // compatibilité mais plus utilisée par les contrôleurs depuis l'ajout du
    // lien direct avis -> prestataire.
    @Query("SELECT a FROM Avis a WHERE a.reservation.prestataire.id = :prestataireId")
    List<Avis> findByReservationPrestataireId(@Param("prestataireId") Long prestataireId);

    // Tous les avis d'un prestataire, qu'ils soient rattachés à une
    // réservation ou laissés directement depuis la page Profil.
    List<Avis> findByPrestataireId(Long prestataireId);
}