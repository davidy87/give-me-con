package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.voucher.Voucher;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoucherForSale extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private LocalDate expDate;

    @Column(nullable = false)
    private String barcode;

    @OneToOne
    @JoinColumn
    private VoucherForSaleImage voucherForSaleImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member seller;

    @Builder
    public VoucherForSale(Long price, LocalDate expDate, String barcode) {
        this.price = price;
        this.expDate = expDate;
        this.barcode = barcode;
    }

    public String getTitle() {
        return voucher.getTitle();
    }

    public String getImageUrl() {
        return voucherForSaleImage.getImageUrl();
    }

    public void updateVoucherForSaleImage(VoucherForSaleImage voucherForSaleImage) {
        this.voucherForSaleImage = voucherForSaleImage;
    }

    public void updateVoucher(Voucher voucher) {
        if (this.voucher != null) {
            this.voucher.deleteVoucherForSale(this);
        }

        this.voucher = voucher;
        voucher.addVoucherForSale(this);
    }

    public void updateSeller(Member seller) {
        this.seller = seller;
    }

    public void delete() {
        voucher.deleteVoucherForSale(this);
        voucherForSaleImage = null;
        voucher = null;
        seller = null;
    }
}
