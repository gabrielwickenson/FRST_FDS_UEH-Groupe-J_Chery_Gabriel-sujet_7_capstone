package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    // Permet de "provisionner" la ligne `client` pour un utilisateur qui
    // existe déjà dans `user` (ex. un compte PRESTATAIRE) mais qui n'a pas
    // encore de ligne dans la table fille `client`. Nécessaire pour laisser
    // n'importe quel compte connecté — client ou pro — réserver un service
    // en tant que client, sans dupliquer la ligne `user` (stratégie JOINED).
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO client (id) VALUES (:id)", nativeQuery = true)
    void provisionClientRow(@Param("id") Long id);
}