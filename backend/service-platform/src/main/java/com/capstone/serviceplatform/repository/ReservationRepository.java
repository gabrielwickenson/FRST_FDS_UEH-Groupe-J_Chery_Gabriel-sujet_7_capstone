package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.prestataire.id = :prestataireId AND r.dateHeure = :dateHeure AND r.statut NOT IN ('ANNULEE', 'TERMINEE')")
    boolean existsConflit(@Param("prestataireId") Long prestataireId, @Param("dateHeure") LocalDateTime dateHeure);
    List<Reservation> findByClientId(Long clientId);
    List<Reservation> findByPrestataireId(Long prestataireId);
    List<Reservation> findByPrestataireIdAndStatut(Long prestataireId, String statut);

    @Query("SELECT r FROM Reservation r WHERE r.prestataire.id = :prestataireId AND r.statut = :statut AND r.dateHeure BETWEEN :start AND :end")
    List<Reservation> findByPrestataireIdAndStatutAndDateHeureBetween(
            @Param("prestataireId") Long prestataireId,
            @Param("statut") String statut,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}