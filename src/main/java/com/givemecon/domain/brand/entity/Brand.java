package com.givemecon.domain.brand.entity;

import com.givemecon.domain.BaseEntity;
import com.givemecon.domain.category.entity.Category;
import com.givemecon.domain.image.entity.BrandIcon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE brand SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToOne
    private BrandIcon brandIcon;

    @ManyToOne(fetch = FetchType.LAZY)
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
