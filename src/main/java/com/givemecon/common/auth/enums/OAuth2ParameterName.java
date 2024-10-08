package com.givemecon.common.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2ParameterName {

    SUCCESS("success"),
    AUTHORIZATION_CODE("authorizationCode");

    private final String name;
}
