package com.givemecon.domain.likedvoucher;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.voucher.Voucher;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LikedVoucher extends BaseTimeEntity {

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
    public LikedVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public void updateMember(Member member) {
        this.member = member;
    }
}
