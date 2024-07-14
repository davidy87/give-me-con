package com.givemecon.domain.purchasedvoucher.entity;

import com.givemecon.domain.BaseEntity;
import com.givemecon.domain.member.entity.Member;
import com.givemecon.domain.purchasedvoucher.dto.PurchasedVoucherStatus;
import com.givemecon.domain.voucher.entity.Voucher;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static com.givemecon.domain.purchasedvoucher.dto.PurchasedVoucherStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE purchased_voucher SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class PurchasedVoucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchasedVoucherStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    public PurchasedVoucher(Voucher voucher, Member owner) {
        this.status = USABLE;
        this.voucher = voucher;
        this.owner = owner;
    }

    public void updateStatus(PurchasedVoucherStatus status) {
        this.status = status;
    }
}
