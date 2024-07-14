package com.givemecon.domain.image.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoucherImage extends ImageEntity {

    @Builder
    public VoucherImage(String imageKey, String imageUrl, String originalName) {
        super(imageKey, imageUrl, originalName);
    }
}