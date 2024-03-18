package com.givemecon.controller;

import com.givemecon.config.auth.dto.TokenInfo;

public abstract class TokenHeaderUtils {

    public static String getAccessTokenHeader(TokenInfo tokenInfo) {
        return tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken();
    }

    public static String getRefreshTokenHeader(TokenInfo tokenInfo) {
        return tokenInfo.getGrantType() + " " + tokenInfo.getRefreshToken();
    }
}
