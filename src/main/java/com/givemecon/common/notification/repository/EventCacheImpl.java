package com.givemecon.common.notification.repository;

import com.givemecon.common.notification.repository.entity.Event;
import com.givemecon.common.notification.util.EventIdUtils;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EventCacheImpl implements EventCache {

    private final Map<String, Event> eventCache = new ConcurrentHashMap<>();

    @Override
    public Event save(String eventId, Event event) {
        eventCache.put(eventId, event);
        return event;
    }

    @Override
    public Optional<Event> findByEventId(String eventId) {
        return Optional.ofNullable(eventCache.get(eventId));
    }

    @Override
    public Map<String, Event> findAllOmittedEvents(String username) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(username))
                .filter(entry -> entry.getKey().compareTo(EventIdUtils.createEventId(username)) < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteByEventId(String eventId) {
        eventCache.remove(eventId);
    }

    @Override
    public void clear() {
        eventCache.clear();
    }
}
