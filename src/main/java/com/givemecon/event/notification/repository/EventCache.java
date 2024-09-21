package com.givemecon.event.notification.repository;

import java.util.Map;
import java.util.Optional;

public interface EventCache {

    Object save(String eventId, Object data);

    Optional<Object> findByEventId(String eventId);

    Map<String, Object> findAllOmittedEvents(String username);

    void deleteByEventId(String eventId);
}
