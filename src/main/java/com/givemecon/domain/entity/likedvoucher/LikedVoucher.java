package com.givemecon.domain.entity.likedvoucher;

import com.givemecon.domain.entity.BaseEntity;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE liked_voucher SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class LikedVoucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private VoucherKind voucherKind;

    @Builder
    public LikedVoucher(Member member, VoucherKind voucherKind) {
        this.member = member;
        this.voucherKind = voucherKind;
    }
}
