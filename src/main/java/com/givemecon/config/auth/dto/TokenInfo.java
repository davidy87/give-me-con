package com.givemecon.config.auth.dto;

import com.givemecon.config.enums.Authority;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class TokenInfo {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private String username;

    private Authority authority;
}
