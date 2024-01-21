package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoucherForSaleImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String imageKey;

    @Column(nullable = false, unique = true)
    private String imageUrl;

    @Column(nullable = false)
    private String originalName;

    @OneToOne(mappedBy = "voucherForSaleImage", fetch = FetchType.LAZY)
    private VoucherForSale voucherForSale;

    @Builder
    public VoucherForSaleImage(String imageKey, String imageUrl, String originalName) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.originalName = originalName;
    }

    public void setVoucherForSale(VoucherForSale voucherForSale) {
        this.voucherForSale = voucherForSale;
    }
}
