package com.givemecon.common.notification.repository;

import com.givemecon.common.notification.repository.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUsername(String username);

    Page<Notification> findPageByUsername(String username, Pageable pageable);

    @Modifying
    @Query("update Notification n set n.deleted = true where n.createdDate < :date")
    void deleteAllByCreatedDateBefore(LocalDateTime date);
}
