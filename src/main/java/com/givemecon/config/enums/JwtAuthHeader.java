package com.givemecon.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtAuthHeader {

    AUTHORIZATION("Authorization"),
    REFRESH_TOKEN("Refresh-Token");

    private final String name;
}
