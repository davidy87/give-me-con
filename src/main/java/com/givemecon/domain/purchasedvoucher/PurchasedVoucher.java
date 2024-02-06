package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.GenerationType.*;

@Getter
@Entity
public class PurchasedVoucher extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isValid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_for_sale_id")
    private VoucherForSale voucherForSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    public PurchasedVoucher() {
        this.isValid = true;
    }

    public void updateVoucherForSale(VoucherForSale voucherForSale) {
        this.voucherForSale = voucherForSale;
    }

    public void updateOwner(Member owner) {
        this.owner = owner;
    }

    public void updateValidity() {
        isValid = !isValid;
    }
}
