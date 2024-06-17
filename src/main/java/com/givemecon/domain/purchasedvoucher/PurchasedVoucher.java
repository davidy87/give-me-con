package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE purchased_voucher SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class PurchasedVoucher extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchasedVoucherStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private VoucherForSale voucherForSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member owner;

    public PurchasedVoucher(VoucherForSale voucherForSale, Member owner) {
        this.status = USABLE;
        this.voucherForSale = voucherForSale;
        this.owner = owner;
    }

    public void updateStatus(PurchasedVoucherStatus status) {
        this.status = status;
    }
}
