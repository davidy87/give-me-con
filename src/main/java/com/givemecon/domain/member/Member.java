package com.givemecon.domain.member;

import com.givemecon.config.auth.OAuth2Provider;
import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private OAuth2Provider provider;

    @Builder
    public Member(String email, String username, Role role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }

    @Builder(builderClassName = "oauthBuilder", builderMethodName = "oauthBuilder")
    public Member(String email, String username, Role role, OAuth2Provider provider) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.provider = provider;
    }

    public Member update(String email, String username) {
        this.email = email;
        this.username = username;

        return this;
    }

    public String getRoleKey() {
        return role.getKey();
    }
}
