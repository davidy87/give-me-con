package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.BaseTimeEntity;
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
    @JoinColumn(name = "voucher_for_sale_image_id")
    private VoucherForSaleImage voucherForSaleImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
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
        this.voucher = voucher;
    }

    public void updateSeller(Member seller) {
        this.seller = seller;
    }
}
