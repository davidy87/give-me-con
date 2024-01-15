package com.givemecon.domain.category;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.brand.Brand;
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

    @Column(length = 500)
    private String icon;

    @OneToOne
    @JoinColumn(name = "category_icon_id")
    private CategoryIcon categoryIcon;

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Brand> brandList = new ArrayList<>();

    @Builder
    public Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void update(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void setCategoryIcon(CategoryIcon categoryIcon) {
        this.categoryIcon = categoryIcon;
        categoryIcon.setCategory(this);
    }

    public void addBrand(Brand brand) {
        brandList.add(brand);
        brand.setCategory(this);
    }
}
