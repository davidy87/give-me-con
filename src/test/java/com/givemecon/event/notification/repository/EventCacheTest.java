package com.givemecon.event.notification.repository;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.event.notification.util.EventIdUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class EventCacheTest extends IntegrationTestEnvironment {

    @Autowired
    EventCache eventCache;

    @Test
    @DisplayName("이벤트 저장 테스트")
    void save() {
        // given
        String eventId = "eventId";
        Object data = "This is notification data";

        // when
        Object saved = eventCache.save(eventId, data);

        // then
        assertThat(saved).isEqualTo(data);
    }

    @Test
    @DisplayName("알림이 누락된 이벤트 데이터 조회 테스트")
    void findAllOmittedEvents() {
        // given
        String username = "tester";
        String eventId = username + "-" + (System.currentTimeMillis() - 1000);
        Object oldData = eventCache.save(eventId, "This is old data.");

        // when
        Map<String, Object> found = eventCache.findAllOmittedEvents(username);

        // then
        assertThat(found.get(eventId)).isEqualTo(oldData);
    }

    @Test
    @DisplayName("이벤트 데이터 삭제 테스트")
    void deleteByEventId() {
        // given
        String eventId = EventIdUtils.createEventId("tester");
        eventCache.save(eventId, "data");

        // when
        eventCache.deleteByEventId(eventId);

        // then
        Optional<Object> found = eventCache.findByEventId(eventId);
        assertThat(found).isEmpty();
    }
}