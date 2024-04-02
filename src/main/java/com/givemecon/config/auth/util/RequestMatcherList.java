package com.givemecon.config.auth.util;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static com.givemecon.config.enums.ApiPathPattern.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

public abstract class RequestMatcherList {

    public static RequestMatcher[] ofPermitAll() {
        return new AntPathRequestMatcher[] {
                antMatcher(ADMIN_LOGIN_API.pattern()),
                antMatcher(GET, CATEGORY_API.pattern()),
                antMatcher(GET, BRAND_API.pattern()),
                antMatcher(GET, VOUCHER_API.pattern()),
                antMatcher(GET, VOUCHER_FOR_SALE_API.pattern())
        };
    }

    public static RequestMatcher[] ofAuthenticated() {
        return new AntPathRequestMatcher[] {
                antMatcher(AUTH_API.pattern())
        };
    }

    public static RequestMatcher[] ofRoleAdmin() {
        return new AntPathRequestMatcher[] {
                antMatcher(MEMBER_API.pattern()),
                antMatcher(CATEGORY_API.pattern()),
                antMatcher(BRAND_API.pattern()),
                antMatcher(VOUCHER_API.pattern())
        };
    }

    public static RequestMatcher[] ofAnyRole() {
        return new AntPathRequestMatcher[] {
                antMatcher(AUTH_API.pattern()),
                antMatcher(VOUCHER_FOR_SALE_API.pattern()),
                antMatcher(LIKED_VOUCHER_API.pattern()),
                antMatcher(PURCHASED_VOUCHER_API.pattern())
        };
    }
}
