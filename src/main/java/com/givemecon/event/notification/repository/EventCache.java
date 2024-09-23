package com.givemecon.event.notification.repository;

import java.util.Map;
import java.util.Optional;

public interface EventCache {

    Event save(String eventId, Event event);

    Optional<Event> findByEventId(String eventId);

    Map<String, Event> findAllOmittedEvents(String username);

    void deleteByEventId(String eventId);

    void clear();
}
