package com.givemecon.domain.entity.category;

import com.givemecon.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE category SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private CategoryIcon categoryIcon;

    @Builder
    public Category(String name, CategoryIcon categoryIcon) {
        this.name = name;
        this.categoryIcon = categoryIcon;
    }

    public String getImageUrl() {
        return categoryIcon.getImageUrl();
    }

    public void updateName(String name) {
        this.name = name;
    }
}
