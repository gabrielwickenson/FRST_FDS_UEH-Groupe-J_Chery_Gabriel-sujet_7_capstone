package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}