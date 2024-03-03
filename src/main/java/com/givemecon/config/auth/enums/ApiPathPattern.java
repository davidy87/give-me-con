package com.givemecon.config.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiPathPattern {

    MEMBER_API_PATH("/api/members/**"),
    ADMIN_LOGIN_API_PATH("/api/members/admin/login"),
    AUTH_API_PATH("/api/auth/**"),
    CATEGORY_API_PATH("/api/categories/**"),
    BRAND_API_PATH("/api/brands/**"),
    VOUCHER_API_PATH("/api/vouchers/**"),
    VOUCHER_FOR_SALE_API_PATH("/api/vouchers-for-sale/**"),
    LIKED_VOUCHER_API_PATH("/api/liked-vouchers/**"),
    PURCHASED_VOUCHER_API_PATH("/api/purchased-vouchers/**");

    private final String pattern;
}
