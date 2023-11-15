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

    @Column(nullable = false)
    private String name;

    private String icon;

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Brand> brands = new ArrayList<>();

    @Builder
    public Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void addBrand(Brand brand) {
        brands.add(brand);
        brand.setCategory(this);
    }
}
