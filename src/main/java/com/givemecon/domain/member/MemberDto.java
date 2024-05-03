package com.givemecon.domain.member;

import com.givemecon.config.enums.Authority;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.givemecon.config.enums.Authority.*;

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

        private final Long id;

        private final String username;

        private final Authority authority;

        public TokenRequest(Member member) {
            this.id = member.getId();
            this.username = member.getUsername();
            this.authority = member.getAuthority();
        }

        public String getRole() {
            return authority.getRole();
        }
    }
}
