package com.givemecon.config.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiPathPattern {

    MEMBER_API("/members/**"),
    ADMIN_LOGIN_API("/members/admin/login"),
    AUTH_API("/auth/**"),
    AUTH_SUCCESS_API("/auth/success"),

    CATEGORY_API("/categories/**"),
    BRAND_API("/brands/**"),
    VOUCHER_API("/vouchers/**"),
    VOUCHER_FOR_SALE_API("/vouchers-for-sale/**"),
    LIKED_VOUCHER_API("/liked-vouchers/**"),
    PURCHASED_VOUCHER_API("/purchased-vouchers/**"),
    IMAGE_TEXT_EXTRACTION_API("/images/extracted-texts");

    private static final String BASE_PATH = "/api";

    private final String pattern;

    public String getPattern() {
        return BASE_PATH + this.pattern;
    }
}
