package com.givemecon.domain.category;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.image.category.CategoryIcon;
import com.givemecon.domain.voucher.Voucher;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_icon_id")
    private CategoryIcon categoryIcon;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Brand> brandList = new ArrayList<>();

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Voucher> voucherList = new ArrayList<>();

    @Builder
    public Category(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return categoryIcon.getImageUrl();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCategoryIcon(CategoryIcon categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public void addBrand(Brand brand) {
        brandList.add(brand);
        brand.updateCategory(this);
    }

    public void addVoucher(Voucher voucher) {
        voucherList.add(voucher);
        voucher.updateCategory(this);
    }
}
