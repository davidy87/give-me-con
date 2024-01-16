package com.givemecon.domain.brand;

import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BrandIcon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String imageKey;

    @Column(nullable = false, unique = true)
    private String imageUrl;

    @Column(nullable = false)
    private String originalName;

    @OneToOne(mappedBy = "brandIcon", fetch = FetchType.LAZY)
    private Brand brand;

    @Builder
    public BrandIcon(String imageKey, String originalName) {
        this.imageKey = imageKey;
        this.originalName = originalName;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}
