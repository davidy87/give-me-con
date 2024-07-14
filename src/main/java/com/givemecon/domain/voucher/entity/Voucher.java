package com.givemecon.domain.voucher.entity;

import com.givemecon.domain.BaseEntity;
import com.givemecon.domain.image.entity.VoucherImage;
import com.givemecon.domain.member.entity.Member;
import com.givemecon.domain.order.entity.Order;
import com.givemecon.domain.voucher.dto.VoucherStatus;
import com.givemecon.domain.voucherkind.entity.VoucherKind;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

import static com.givemecon.domain.voucher.dto.VoucherStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE voucher SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class Voucher extends BaseEntity {

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
    private VoucherStatus status;

    @Column(nullable = false)
    private LocalDate saleRequestedDate;

    @OneToOne(fetch = FetchType.LAZY)
    private VoucherImage voucherImage;

    @ManyToOne(fetch = FetchType.LAZY)
    private VoucherKind voucherKind;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Builder
    public Voucher(Long price, LocalDate expDate, String barcode) {
        this.price = price;
        this.expDate = expDate;
        this.barcode = barcode;
        this.status = NOT_YET_PERMITTED;
        this.saleRequestedDate = LocalDate.now();
    }

    public String getTitle() {
        return voucherKind.getTitle();
    }

    public String getImageUrl() {
        return voucherImage.getImageUrl();
    }

    public void updateStatus(VoucherStatus status) {
        this.status = status;
    }

    public void updateVoucherImage(VoucherImage voucherImage) {
        this.voucherImage = voucherImage;
    }

    public void updateVoucherKind(VoucherKind voucherKind) {
        this.voucherKind = voucherKind;
    }

    public void updateSeller(Member seller) {
        this.seller = seller;
    }

    public void updateOrder(Order order) {
        this.order = order;
    }
}