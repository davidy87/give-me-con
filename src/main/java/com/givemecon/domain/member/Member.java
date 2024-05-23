package com.givemecon.domain.member;

import com.givemecon.config.enums.OAuth2Provider;
import com.givemecon.config.enums.Authority;
import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Authority authority;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private OAuth2Provider provider;

    @Builder
    public Member(String email, String username, String password, Authority authority) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.authority = authority;
    }

    @Builder(builderClassName = "oauthBuilder", builderMethodName = "oauthBuilder")
    public Member(String email, String username, Authority authority, OAuth2Provider provider) {
        this.email = email;
        this.username = username;
        this.authority = authority;
        this.provider = provider;
    }

    public Member update(String email, String username) {
        this.email = email;
        this.username = username;
        return this;
    }

    public String getRole() {
        return authority.getRole();
    }
}
