package com.givemecon.common.auth.enums;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public enum TokenDuration {

    ACCESS_TOKEN_DURATION(Duration.ofMinutes(30)),
    REFRESH_TOKEN_DURATION(Duration.ofDays(14));

    private final Duration duration;

    public Long toMillis() {
        return this.duration.toMillis();
    }
}
