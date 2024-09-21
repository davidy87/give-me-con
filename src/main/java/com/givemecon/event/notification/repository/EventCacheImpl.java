package com.givemecon.event.notification.repository;

import com.givemecon.event.notification.util.EventIdUtils;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EventCacheImpl implements EventCache {

    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public Object save(String eventId, Object data) {
        eventCache.put(eventId, data);
        return data;
    }

    @Override
    public Optional<Object> findByEventId(String eventId) {
        return Optional.ofNullable(eventCache.get(eventId));
    }

    @Override
    public Map<String, Object> findAllOmittedEvents(String username) {
        return eventCache.entrySet().stream()
                .filter(event -> event.getKey().startsWith(username))
                .filter(event -> event.getKey().compareTo(EventIdUtils.createEventId(username)) < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteByEventId(String eventId) {
        if (eventCache.remove(eventId) == null) {
            throw new RuntimeException(); // TODO: 예외 처리
        }
    }
}
