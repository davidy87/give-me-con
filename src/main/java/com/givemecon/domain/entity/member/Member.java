package com.givemecon.domain.entity.member;

import com.givemecon.common.auth.enums.OAuth2Provider;
import com.givemecon.domain.entity.BaseEntity;
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
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private OAuth2Provider provider;

    @Builder
    public Member(String email, String username, String password, Role role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Builder(builderClassName = "oauthBuilder", builderMethodName = "oauthBuilder")
    public Member(String email, String username, Role role, OAuth2Provider provider) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.provider = provider;
    }

    public void update(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getAuthority() {
        return role.getAuthority();
    }
}
