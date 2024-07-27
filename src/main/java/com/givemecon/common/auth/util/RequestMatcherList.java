package com.givemecon.common.auth.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static com.givemecon.common.auth.enums.ApiPathPattern.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMatcherList {

    public static RequestMatcher[] ofPermitAll() {
        return new AntPathRequestMatcher[] {
                antMatcher(ADMIN_LOGIN_API.getPattern()),
                antMatcher(AUTH_SUCCESS_API.getPattern()),
                antMatcher(GET, CATEGORY_API.getPattern()),
                antMatcher(GET, BRAND_API.getPattern()),
                antMatcher(GET, VOUCHER_KIND_API.getPattern())
        };
    }

    public static RequestMatcher[] ofAuthenticated() {
        return new AntPathRequestMatcher[] {
                antMatcher(TOKEN_REISSUE_API.getPattern())
        };
    }

    public static RequestMatcher[] ofRoleAdmin() {
        return new AntPathRequestMatcher[] {
                antMatcher(MEMBER_API.getPattern()),
                antMatcher(CATEGORY_API.getPattern()),
                antMatcher(BRAND_API.getPattern()),
                antMatcher(VOUCHER_KIND_API.getPattern()),
                antMatcher(ADMIN_API.getPattern())
        };
    }

    public static RequestMatcher[] ofAnyRole() {
        return new AntPathRequestMatcher[] {
                antMatcher(TOKEN_REISSUE_API.getPattern()),
                antMatcher(VOUCHER_API.getPattern()),
                antMatcher(LIKED_VOUCHER_API.getPattern()),
                antMatcher(PURCHASED_VOUCHER_API.getPattern()),
                antMatcher(ORDER_API.getPattern()),
                antMatcher(PAYMENT_API.getPattern()),
                antMatcher(IMAGE_TEXT_EXTRACTION_API.getPattern())
        };
    }
}
