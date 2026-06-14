package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}