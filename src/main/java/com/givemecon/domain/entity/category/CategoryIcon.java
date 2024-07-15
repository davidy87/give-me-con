package com.givemecon.domain.entity.category;

import com.givemecon.domain.entity.ImageEntity;
import jakarta.persistence.Entity;
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
