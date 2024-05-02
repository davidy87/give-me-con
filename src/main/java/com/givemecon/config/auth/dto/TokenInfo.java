package com.givemecon.config.auth.dto;

import com.givemecon.config.enums.Role;
import lombok.*;

@Getter
@NoArgsConstructor
public class TokenInfo {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private String username;

    private Role role;

    @Builder
    public TokenInfo(String grantType, String accessToken, String refreshToken, String username, Role role) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
    }
}
