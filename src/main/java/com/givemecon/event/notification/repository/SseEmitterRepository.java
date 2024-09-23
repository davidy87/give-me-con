package com.givemecon.event.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

public interface SseEmitterRepository {

    SseEmitter save(String username, SseEmitter sseEmitter);

    Optional<SseEmitter> findByUsername(String username);

    void deleteByUsername(String username);

    void clear();
}
