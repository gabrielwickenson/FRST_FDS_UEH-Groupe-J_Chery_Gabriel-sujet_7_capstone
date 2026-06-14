package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AvisRepository extends JpaRepository<Avis, Long> {
    @Query("SELECT a FROM Avis a WHERE a.reservation.prestataire.id = :prestataireId")
    List<Avis> findByReservationPrestataireId(@Param("prestataireId") Long prestataireId);
}