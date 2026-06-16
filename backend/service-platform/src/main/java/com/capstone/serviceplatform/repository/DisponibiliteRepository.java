package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Disponibilite;
import com.capstone.serviceplatform.entity.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findByPrestataire(Prestataire prestataire);
}