package com.givemecon.domain.image.category;

import com.givemecon.domain.image.ImageEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CategoryIcon extends ImageEntity {

    @Builder
    public CategoryIcon(String imageKey, String imageUrl, String originalName) {
        super(imageKey, imageUrl, originalName);
    }
}
