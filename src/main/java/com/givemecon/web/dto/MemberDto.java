package com.givemecon.web.dto;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.Role;
import lombok.Builder;
import lombok.Getter;

public class MemberDto {

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
                    .role(Role.ADMIN)
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

}
