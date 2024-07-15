package com.givemecon.common.auth.dto;

import com.givemecon.domain.entity.member.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
