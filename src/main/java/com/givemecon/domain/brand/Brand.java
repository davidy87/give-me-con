package com.givemecon.domain.brand;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.image.brand.BrandIcon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToOne
    @JoinColumn
    private BrandIcon brandIcon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Category category;

    @Builder
    public Brand(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return brandIcon.getImageUrl();
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
}
