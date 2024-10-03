package com.givemecon.event.notification.repository;

import com.givemecon.event.notification.repository.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUsername(String username);

    Page<Notification> findPageByUsername(String username, Pageable pageable);
}
