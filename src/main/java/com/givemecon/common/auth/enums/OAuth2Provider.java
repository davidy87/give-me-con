package com.givemecon.common.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {

    GOOGLE("given_name", "email"),
    NAVER("nickname", "email"),
    KAKAO("nickname", "email");

    private final String usernameAttributeKey;

    private final String emailAttributeKey;
}
