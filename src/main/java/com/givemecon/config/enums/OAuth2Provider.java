package com.givemecon.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {

    GOOGLE("given_name"),
    NAVER("nickname"),
    KAKAO("nickname");

    private final String usernameAttributeKey;
}
