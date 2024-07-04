package com.givemecon.domain.likedvoucher;

import com.givemecon.domain.BaseEntity;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.voucher.Voucher;
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
    @JoinColumn
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Voucher voucher;

    @Builder
    public LikedVoucher(Member member, Voucher voucher) {
        this.member = member;
        this.voucher = voucher;
    }
}
