package com.givemecon.domain.entity.brand;

import com.givemecon.domain.entity.ImageEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BrandIcon extends ImageEntity {

    @Builder
    public BrandIcon(String imageKey, String imageUrl, String originalName) {
        super(imageKey, imageUrl, originalName);
    }
}
