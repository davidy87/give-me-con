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
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String icon;

    @Builder
    public Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void update(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }
}
