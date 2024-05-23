package com.givemecon.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionAttribute {

    TOKEN_INFO("tokenInfo", 10);

    private final String name;

    private final int duration;
}
