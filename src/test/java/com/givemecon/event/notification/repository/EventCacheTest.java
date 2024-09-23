package com.givemecon.event.notification.repository;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.event.notification.util.EventIdUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

import static com.givemecon.event.notification.util.EventType.SALE_CONFIRMATION;
import static org.assertj.core.api.Assertions.*;

class EventCacheTest extends IntegrationTestEnvironment {

    @Autowired
    EventCache eventCache;

    @Test
    @DisplayName("이벤트 저장 테스트")
    void save() {
        // given
        String eventId = "eventId";
        Event event = new Event(SALE_CONFIRMATION.getEventName(), "Sale confirmed.");

        // when
        Event saved = eventCache.save(eventId, event);

        // then
        assertThat(saved).isEqualTo(event);
    }

    @Test
    @DisplayName("알림이 누락된 이벤트 데이터 조회 테스트")
    void findAllOmittedEvents() {
        // given
        String username = "tester";
        String eventId = username + "-" + (System.currentTimeMillis() - 1000);
        Event oldEvent = eventCache.save(eventId, new Event(SALE_CONFIRMATION.getEventName(), "Sale confirmed."));

        // when
        Map<String, Event> found = eventCache.findAllOmittedEvents(username);

        // then
        assertThat(found.get(eventId)).isEqualTo(oldEvent);
    }

    @Test
    @DisplayName("이벤트 데이터 삭제 테스트")
    void deleteByEventId() {
        // given
        String eventId = EventIdUtils.createEventId("tester");
        eventCache.save(eventId, new Event(SALE_CONFIRMATION.getEventName(), "Sale confirmed."));

        // when
        eventCache.deleteByEventId(eventId);

        // then
        Optional<Event> found = eventCache.findByEventId(eventId);
        assertThat(found).isEmpty();
    }
}