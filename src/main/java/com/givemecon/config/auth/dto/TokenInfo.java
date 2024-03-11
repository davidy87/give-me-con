package com.givemecon.config.auth.dto;

import com.givemecon.config.auth.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class TokenInfo {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private Role role;
}
