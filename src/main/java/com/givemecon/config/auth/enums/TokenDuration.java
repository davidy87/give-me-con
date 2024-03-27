package com.givemecon.config.auth.enums;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public enum TokenDuration {

    ACCESS_TOKEN_DURATION(Duration.ofMinutes(30).toMillis()),
    REFRESH_TOKEN_DURATION(Duration.ofDays(14).toMillis());

    private final Long duration;

    public Long duration() {
        return duration;
    }
}
