package com.givemecon.domain.brand;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.category.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String icon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Brand(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void update(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
