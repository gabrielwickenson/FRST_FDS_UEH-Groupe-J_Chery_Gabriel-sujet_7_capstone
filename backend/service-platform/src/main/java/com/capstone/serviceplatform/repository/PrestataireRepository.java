package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PrestataireRepository extends JpaRepository<Prestataire, Long> {

    @Query("SELECT p FROM Prestataire p WHERE " +
            "(:service IS NULL OR LOWER(p.competences) LIKE LOWER(CONCAT('%', :service, '%'))) AND " +
            "(:noteMin IS NULL OR p.moyenneNotes >= :noteMin) AND " +
            "(:zone IS NULL OR LOWER(p.zoneIntervention) LIKE LOWER(CONCAT('%', :zone, '%')))")
    List<Prestataire> rechercherParFiltres(@Param("service") String service,
                                           @Param("noteMin") Double noteMin,
                                           @Param("zone") String zone);
}