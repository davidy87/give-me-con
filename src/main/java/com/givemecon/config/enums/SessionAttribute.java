package com.givemecon.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionAttribute {

    TOKEN_INFO(10);

    private final int duration;
}
