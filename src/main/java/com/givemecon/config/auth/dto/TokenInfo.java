package com.givemecon.config.auth.dto;

import com.givemecon.config.auth.enums.Role;
import lombok.*;

@ToString
@Getter
public class TokenInfo {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private Role role;

    public TokenInfo() {
    }

    @Builder
    public TokenInfo(String grantType, String accessToken, String refreshToken, Role role) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }
}
