package com.givemecon.common.notification.repository;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.common.notification.repository.entity.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class NotificationRepositoryTest extends IntegrationTestEnvironment {

    @Autowired
    NotificationRepository notificationRepository;

    @Test
    @DisplayName("Notification 저장 테스트")
    void save() {
        // given
        Notification notification = new Notification("user", "This is notification.");

        // when
        Notification saved = notificationRepository.save(notification);

        // then
        assertThat(saved.getContent()).isEqualTo(notification.getContent());
    }

    @Test
    @DisplayName("Notification 조회 테스트 1 - id로 조회")
    void findById() {
        // given
        Notification notification =
                notificationRepository.saveAndFlush(new Notification("user", "This is notification."));

        // when
        Optional<Notification> found = notificationRepository.findById(notification.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(notification);
    }

    @Test
    @DisplayName("Notification 조회 테스트 2 - username으로 전체 조회")
    void findByUsername() {
        // given
        Notification notification =
                notificationRepository.saveAndFlush(new Notification("user", "This is notification."));

        // when
        List<Notification> notificationList = notificationRepository.findAllByUsername(notification.getUsername());

        // then
        assertThat(notificationList).hasSize(1);
        assertThat(notificationList.get(0)).isEqualTo(notification);
    }

    @Test
    @DisplayName("Notification 삭제 테스트")
    void delete() {
        // given
        Notification notification =
                notificationRepository.saveAndFlush(new Notification("user", "This is notification."));

        // when
        notificationRepository.delete(notification);

        // then
        Optional<Notification> found = notificationRepository.findById(notification.getId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("특정 날짜 이전의 createdDate 값을 갖고 있는 모든 Notification 삭제 테스트")
    void deleteAllByCreatedDateBefore() {
        // given
        notificationRepository.saveAndFlush(new Notification("user", "This is notification."));
        LocalDateTime nextDay = LocalDateTime.now().plusDays(1);

        // when
        notificationRepository.deleteAllByCreatedDateBefore(nextDay);

        // then
        List<Notification> result = notificationRepository.findAll();
        assertThat(result).isEmpty();
    }
}