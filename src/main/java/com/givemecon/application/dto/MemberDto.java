package com.givemecon.application.dto;

import com.givemecon.config.enums.Authority;
import com.givemecon.domain.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.givemecon.config.enums.Authority.ADMIN;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberDto {

    @Getter
    @Builder
    public static class SignupRequest {

        private final String email;

        private final String username;

        private final String password;

        private final String passwordConfirm;

        public Member toEntity(String encodedPassword) {
            return Member.builder()
                    .email(email)
                    .username(username)
                    .password(encodedPassword)
                    .authority(ADMIN)
                    .build();
        }
    }

    @Getter
    public static class SignupResponse {

        private final String username;

        public SignupResponse(Member member) {
            this.username = member.getUsername();
        }
    }

    @Getter
    @Builder
    public static class LoginRequest {

        private final String email;

        private final String password;
    }

    @Getter
    public static class TokenRequest {

        private final Long memberId;

        private final String username;

        private final Authority authority;

        public TokenRequest(Member member) {
            this.memberId = member.getId();
            this.username = member.getUsername();
            this.authority = member.getAuthority();
        }

        public String getRole() {
            return authority.getRole();
        }
    }
}
