package com.givemecon.common.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GrantType {

    BEARER("Bearer");

    private final String type;
}
