package com.givemecon.common.notification.repository;

import com.givemecon.common.notification.repository.entity.Event;

import java.util.Map;
import java.util.Optional;

public interface EventCache {

    Event save(String eventId, Event event);

    Optional<Event> findByEventId(String eventId);

    Map<String, Event> findAllOmittedEvents(String username);

    void deleteByEventId(String eventId);

    void clear();
}
