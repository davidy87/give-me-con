package com.givemecon.domain.member;

import com.givemecon.config.enums.OAuth2Provider;
import com.givemecon.config.enums.Authority;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "member")
    List<LikedVoucher> likedVoucherList = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    List<PurchasedVoucher> purchasedVoucherList = new ArrayList<>();

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

    public void addLikedVoucher(LikedVoucher likedVoucher) {
        likedVoucherList.add(likedVoucher);
        likedVoucher.updateMember(this);
    }

    public void addPurchasedVoucher(PurchasedVoucher purchasedVoucher) {
        if (purchasedVoucher == null) {
            return;
        }

        if (!purchasedVoucherList.contains(purchasedVoucher)) {
            purchasedVoucherList.add(purchasedVoucher);
        }
    }

    public void deletePurchasedVoucher(PurchasedVoucher purchasedVoucher) {
        if (purchasedVoucher == null) {
            return;
        }

        purchasedVoucherList.remove(purchasedVoucher);
    }
}
