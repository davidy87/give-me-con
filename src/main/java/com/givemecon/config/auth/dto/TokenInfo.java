package com.givemecon.config.auth.dto;

import com.givemecon.config.enums.Authority;
import lombok.*;

@Getter
@NoArgsConstructor
public final class TokenInfo {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private String username;

    private Authority authority;

    @Builder
    public TokenInfo(String grantType, String accessToken, String refreshToken, String username, Authority authority) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.authority = authority;
    }
}
