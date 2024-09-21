package com.givemecon.event.notification.repository;

import com.givemecon.IntegrationTestEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class SseEmitterRepositoryTest extends IntegrationTestEnvironment {

    @Autowired
    SseEmitterRepository sseEmitterRepository;

    @Test
    @DisplayName("SseEmitter 저장 테스트")
    void save() {
        // given
        String username = "tester";
        SseEmitter sseEmitter = new SseEmitter();

        // when
        SseEmitter saved = sseEmitterRepository.save(username, sseEmitter);

        // then
        assertThat(saved).isEqualTo(sseEmitter);
    }

    @Test
    @DisplayName("SseEmitter 조회 테스트")
    void findByUsername() {
        // given
        String username = "tester";
        SseEmitter sseEmitter = sseEmitterRepository.save(username, new SseEmitter());

        // when
        Optional<SseEmitter> found = sseEmitterRepository.findByUsername(username);

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(sseEmitter);
    }

    @Test
    @DisplayName("SseEmitter 삭제 테스트")
    void deleteByUsername() {
        // given
        String username = "tester";
        sseEmitterRepository.save(username, new SseEmitter());

        // when
        sseEmitterRepository.deleteByUsername(username);

        // then
        Optional<SseEmitter> found = sseEmitterRepository.findByUsername(username);
        assertThat(found).isEmpty();
    }
}