package com.givemecon.config.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NameAttributeKey {

    GOOGLE_USERNAME_KEY("given_name"),
    NAVER_USERNAME_KEY("nickname"),
    KAKAO_USERNAME_KEY("nickname");

    private final String key;
}
