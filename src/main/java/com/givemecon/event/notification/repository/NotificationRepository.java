package com.givemecon.event.notification.repository;

import com.givemecon.event.notification.repository.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByUsername(String username);
}
