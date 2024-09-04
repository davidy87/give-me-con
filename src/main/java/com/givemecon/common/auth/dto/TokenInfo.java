package com.givemecon.common.auth.dto;

import com.givemecon.domain.entity.member.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo implements Serializable {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private String username;

    private Role role;
}
