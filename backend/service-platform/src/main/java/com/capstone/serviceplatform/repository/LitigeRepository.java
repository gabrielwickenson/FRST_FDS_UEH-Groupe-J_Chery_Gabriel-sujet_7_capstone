package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Litige;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LitigeRepository extends JpaRepository<Litige, Long> {
    boolean existsByReservationId(Long reservationId);
    List<Litige> findByStatut(String statut);
}