package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface PrestataireRepository extends JpaRepository<Prestataire, Long> {

    @Query("SELECT p FROM Prestataire p WHERE " +
            "(:service IS NULL OR LOWER(p.competences) LIKE LOWER(CONCAT('%', :service, '%'))) AND " +
            "(:noteMin IS NULL OR p.moyenneNotes >= :noteMin) AND " +
            "(:zone IS NULL OR LOWER(p.zoneIntervention) LIKE LOWER(CONCAT('%', :zone, '%')))")
    List<Prestataire> rechercherParFiltres(@Param("service") String service,
                                           @Param("noteMin") Double noteMin,
                                           @Param("zone") String zone);

    // Symétrique de ClientRepository.provisionClientRow : permet de
    // "provisionner" la ligne `prestataire` pour un compte qui existe déjà
    // dans `user` (rôle PRESTATAIRE) mais dont la ligne fille `prestataire`
    // est manquante (ex. compte créé/modifié en dehors du flux d'inscription
    // normal). Sans ça, tous les endpoints prestataire (profil, tarif,
    // disponibilités, statistiques, revenus) renvoient 404 "Prestataire non
    // trouvé" même pour un compte dont le rôle affiché est bien PRESTATAIRE.
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO prestataire (id) VALUES (:id)", nativeQuery = true)
    void provisionPrestataireRow(@Param("id") Long id);
}