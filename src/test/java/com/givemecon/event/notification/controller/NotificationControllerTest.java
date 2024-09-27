package com.givemecon.event.notification.controller;

import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.ControllerTestEnvironment;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.event.notification.repository.entity.Event;
import com.givemecon.event.notification.repository.EventCache;
import com.givemecon.event.notification.util.EventIdUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.givemecon.application.dto.MemberDto.*;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.domain.entity.member.Role.USER;
import static com.givemecon.event.notification.util.EventType.SSE_SUBSCRIPTION;
import static com.givemecon.event.notification.util.EventType.VOUCHER_STATUS_UPDATE;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequest;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest extends ControllerTestEnvironment {

    @Autowired
    EventCache eventCache;

    @AfterEach
    void tearDown() {
        eventCache.clear();
    }

    @Test
    @DisplayName("SSE 구독 요청 테스트 1 - 첫 연결 요청 or Last-Event-Id가 없는 경우")
    void subscribe() throws Exception {
        // given
        Member user = memberRepository.save(Member.builder()
                .email("user@gmail.com")
                .username("user")
                .role(USER)
                .build());

        TokenInfo tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(user));

        // when
        ResultActions response = mockMvc.perform(get("/api/notification/subscribe")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        String expectedResponseBody =
                "id:\n" +
                "event:" + SSE_SUBSCRIPTION.getEventName() + "\n" +
                "data:SSE connected. Connected user = " + user.getUsername() + "\n\n";

        response.andExpect(status().isOk())
                .andExpect(content().string(expectedResponseBody))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseBody())
                );
    }

    @Test
    @DisplayName("SSE 구독 요청 테스트 2 - Last-Event-Id 헤더 포함")
    void subscribeWithLastEventId() throws Exception {
        // given
        Member user = memberRepository.save(Member.builder()
                .email("user@gmail.com")
                .username("user")
                .role(USER)
                .build());

        TokenInfo tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(user));
        int numOldEvents = 5;

        for (int i = 1; i <= numOldEvents; i++) {
            String oldEventId = user.getUsername() + "-" + (System.currentTimeMillis() - 1000L * i);
            eventCache.save(oldEventId, new Event(VOUCHER_STATUS_UPDATE.getEventName(), "Item " + i + "sale confirmed."));
        }

        String lastEventId = EventIdUtils.createEventId(user.getUsername());

        // when
        ResultActions response = mockMvc.perform(get("/api/notification/subscribe")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .header("Last-Event-Id", lastEventId));

        // then
        String content = response.andReturn().getResponse().getContentAsString();
        int streamSize = content.split("\n\n").length;

        Assertions.assertThat(streamSize).isEqualTo(numOldEvents);

        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseBody())
                );
    }

    @Test
    @DisplayName("SSE 구독 요청 예외 - 구독 요청 시, 사용자의 정보가 없을 경우 예외를 던진다.")
    void invalidSubscriber() throws Exception {
        // when
        ResultActions response = mockMvc.perform(get("/api/notification/subscribe")
                .contentType(MediaType.TEXT_EVENT_STREAM));

        // then
        response.andExpect(status().isBadRequest());
    }
}