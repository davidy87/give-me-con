package com.givemecon.domain.entity.brand;

import com.givemecon.domain.entity.BaseEntity;
import com.givemecon.domain.entity.category.Category;
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

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private BrandIcon brandIcon;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Builder
    public Brand(String name, BrandIcon brandIcon, Category category) {
        this.name = name;
        this.brandIcon = brandIcon;
        this.category = category;
    }

    public String getImageUrl() {
        return brandIcon.getImageUrl();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}
