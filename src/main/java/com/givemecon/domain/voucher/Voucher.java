package com.givemecon.domain.voucher;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.category.Category;
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

    @Column(nullable = false, name = "min_price")
    private Long price;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String caution;

    @OneToOne
    @JoinColumn(name = "voucher_id")
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
    public Voucher(String title, Long price, String description, String caution) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.caution = caution;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updatePrice(Long price) {
        this.price = price;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateCaution(String caution) {
        this.caution = caution;
    }

    public void setVoucherImage(VoucherImage voucherImage) {
        this.voucherImage = voucherImage;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void addVoucherForSale(VoucherForSale voucherForSale) {
        voucherForSaleList.add(voucherForSale);
        voucherForSale.setVoucher(this);
    }
}
