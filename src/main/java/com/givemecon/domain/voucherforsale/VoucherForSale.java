package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.order.Order;
import com.givemecon.domain.voucher.Voucher;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE voucher_for_sale SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherForSaleStatus status;

    @Column(nullable = false)
    private LocalDate saleRequestedDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private VoucherForSaleImage voucherForSaleImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Order order;

    @Builder
    public VoucherForSale(Long price, LocalDate expDate, String barcode) {
        this.price = price;
        this.expDate = expDate;
        this.barcode = barcode;
        this.status = NOT_YET_PERMITTED;
        this.saleRequestedDate = LocalDate.now();
    }

    public String getTitle() {
        return voucher.getTitle();
    }

    public String getImageUrl() {
        return voucherForSaleImage.getImageUrl();
    }

    public void updateStatus(VoucherForSaleStatus status) {
        this.status = status;
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

    public void updateOrder(Order order) {
        this.order = order;
    }
}
