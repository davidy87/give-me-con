package com.givemecon.web.dto;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.Role;
import lombok.Builder;
import lombok.Getter;

public class MemberDto {

    @Getter
    @Builder
    public static class SignupRequest {

        private String email;

        private String username;

        private String password;

        private String passwordConfirm;

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

        private String username;

        public SignupResponse(Member member) {
            this.username = member.getUsername();
        }
    }

    @Getter
    @Builder
    public static class LoginRequest {

        private String email;

        private String password;
    }

}
