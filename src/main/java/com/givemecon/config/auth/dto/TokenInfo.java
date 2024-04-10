package com.givemecon.config.auth.dto;

import com.givemecon.config.enums.Role;
import lombok.*;

@Getter
@NoArgsConstructor
public class TokenInfo {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private Role role;

    @Builder
    public TokenInfo(String grantType, String accessToken, String refreshToken, Role role) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }
}
