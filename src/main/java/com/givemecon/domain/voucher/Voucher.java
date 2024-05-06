package com.givemecon.domain.voucher;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Voucher extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private Long minPrice;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String caution;

    @OneToOne
    @JoinColumn(name = "voucher_image_id")
    private VoucherImage voucherImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(
            mappedBy = "voucher",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<VoucherForSale> voucherForSaleList = new ArrayList<>();

    @Builder
    public Voucher(String title, String description, String caution) {
        this.title = title;
        this.minPrice = 0L;
        this.description = description;
        this.caution = caution;
    }

    public String getImageUrl() {
        return voucherImage.getImageUrl();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateCaution(String caution) {
        this.caution = caution;
    }

    public void updateVoucherImage(VoucherImage voucherImage) {
        this.voucherImage = voucherImage;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateBrand(Brand brand) {
        this.brand = brand;
    }

    public void addVoucherForSale(VoucherForSale voucherForSale) {
        voucherForSaleList.add(voucherForSale);
        voucherForSale.updateVoucher(this);

        if (this.minPrice == 0L) {
            this.minPrice = voucherForSale.getPrice();
        } else {
            this.minPrice = Math.min(this.minPrice, voucherForSale.getPrice());
        }
    }

    public void removeVoucherForSale(VoucherForSale voucherForSale) {
        voucherForSaleList.remove(voucherForSale);
        voucherForSale.updateVoucher(null);

        this.minPrice = voucherForSaleList.stream()
                .map(VoucherForSale::getPrice)
                .mapToLong(Long::longValue)
                .min()
                .orElse(0L);
    }
}
