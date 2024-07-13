package com.givemecon.util;

import com.givemecon.config.auth.dto.TokenInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenHeaderUtils {

    public static String getAccessTokenHeader(TokenInfo tokenInfo) {
        return tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken();
    }

    public static String getRefreshTokenHeader(TokenInfo tokenInfo) {
        return tokenInfo.getGrantType() + " " + tokenInfo.getRefreshToken();
    }
}
