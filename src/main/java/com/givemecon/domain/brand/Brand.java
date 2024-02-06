package com.givemecon.domain.brand;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.voucher.Voucher;
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
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "brand_icon_id")
    private BrandIcon brandIcon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(
            mappedBy = "brand",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Voucher> voucherList = new ArrayList<>();

    @Builder
    public Brand(String name) {
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateBrandIcon(BrandIcon brandIcon) {
        this.brandIcon = brandIcon;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void addVoucher(Voucher voucher) {
        voucherList.add(voucher);
        voucher.updateBrand(this);
    }
}
