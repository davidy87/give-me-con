package com.givemecon.config.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientUrl {

    BASE_URL("http://localhost:8081"),
    LOGIN_URL(BASE_URL.url + "/login");

    private final String url;
}
