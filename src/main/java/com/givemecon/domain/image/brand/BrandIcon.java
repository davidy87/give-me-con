package com.givemecon.domain.image.brand;

import com.givemecon.domain.image.ImageEntity;
import jakarta.persistence.*;
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
