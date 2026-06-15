package com.capstone.serviceplatform.repository;

import com.capstone.serviceplatform.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByDateDesc(Long userId);
}