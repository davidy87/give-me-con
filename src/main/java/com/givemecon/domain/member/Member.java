package com.givemecon.domain.member;

import com.givemecon.config.enums.OAuth2Provider;
import com.givemecon.config.enums.Role;
import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.likedvoucher.LikedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private OAuth2Provider provider;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    List<LikedVoucher> likedVoucherList = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    List<PurchasedVoucher> purchasedVoucherList = new ArrayList<>();

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

    public Member update(String email, String username) {
        this.email = email;
        this.username = username;

        return this;
    }

    public String getRoleKey() {
        return role.getKey();
    }

    public void addLikedVoucher(LikedVoucher likedVoucher) {
        likedVoucherList.add(likedVoucher);
        likedVoucher.updateMember(this);
    }

    public void addPurchasedVoucher(PurchasedVoucher purchasedVoucher) {
        purchasedVoucherList.add(purchasedVoucher);
        purchasedVoucher.updateOwner(this);
    }
}
