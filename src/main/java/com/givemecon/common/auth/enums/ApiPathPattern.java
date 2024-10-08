package com.givemecon.common.auth.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiPathPattern {

    MEMBER_API("/members/**"),
    AUTH_SUCCESS_API("/auth/success"),
    TOKEN_REISSUE_API("/auth/reissue"),

    CATEGORY_API("/categories/**"),
    BRAND_API("/brands/**"),
    VOUCHER_KIND_API("/voucher-kinds/**"),
    VOUCHER_API("/vouchers/**"),
    LIKED_VOUCHER_API("/liked-vouchers/**"),
    PURCHASED_VOUCHER_API("/purchased-vouchers/**"),
    ORDER_API("/orders/**"),
    PAYMENT_API("/payments/**"),
    IMAGE_TEXT_EXTRACTION_API("/images/extracted-texts"),
    SSE_SUBSCRIPTION_API("/sse/subscribe"),
    SSE_NOTIFICATION_API("/sse/notifications"),

    ADMIN_API("/admin/**"),
    ADMIN_SIGNUP_API("/admin/members/signup"),
    ADMIN_LOGIN_API("/admin/members/login");

    private static final String BASE_PATH = "/api";

    private final String pattern;

    public String getPattern() {
        return BASE_PATH + this.pattern;
    }
}
