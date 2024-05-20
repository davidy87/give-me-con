package com.givemecon.domain.category;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.image.category.CategoryIcon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JoinColumn
    private CategoryIcon categoryIcon;

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
}
