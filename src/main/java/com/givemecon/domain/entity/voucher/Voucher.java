package com.givemecon.domain.entity.voucher;

import com.givemecon.domain.entity.BaseEntity;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.order.Order;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

import static com.givemecon.domain.entity.voucher.VoucherStatus.NOT_YET_PERMITTED;

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
    public Voucher(Long price,
                   LocalDate expDate,
                   String barcode,
                   VoucherImage voucherImage,
                   VoucherKind voucherKind,
                   Member seller) {

        this.price = price;
        this.expDate = expDate;
        this.barcode = barcode;
        this.status = NOT_YET_PERMITTED;
        this.saleRequestedDate = LocalDate.now();
        this.voucherImage = voucherImage;
        this.voucherKind = voucherKind;
        this.seller = seller;
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

    public void updateOrder(Order order) {
        this.order = order;
    }
}
