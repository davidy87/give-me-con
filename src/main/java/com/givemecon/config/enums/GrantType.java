package com.givemecon.config.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GrantType {

    BEARER("Bearer");

    private final String type;
}
