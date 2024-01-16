package com.givemecon.domain.category;

import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CategoryIcon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String imageKey;

    @Column(nullable = false, unique = true)
    private String imageUrl;

    @Column(nullable = false)
    private String originalName;

    @OneToOne(mappedBy = "categoryIcon", fetch = FetchType.LAZY)
    private Category category;

    @Builder
    public CategoryIcon(String imageKey, String originalName) {
        this.imageKey = imageKey;
        this.originalName = originalName;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
