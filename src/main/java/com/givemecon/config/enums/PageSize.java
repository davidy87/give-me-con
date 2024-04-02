package com.givemecon.config.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PageSize {

    PAGE_SIZE(10);

    private final int size;

    public int size() {
        return size;
    }
}
