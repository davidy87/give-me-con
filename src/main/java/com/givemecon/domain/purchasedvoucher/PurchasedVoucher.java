package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@SQLDelete(sql = "UPDATE purchased_voucher SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class PurchasedVoucher extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isValid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private VoucherForSale voucherForSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
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
