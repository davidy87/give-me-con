package com.givemecon.domain.image.voucherkind;

import com.givemecon.domain.image.ImageEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoucherKindImage extends ImageEntity {

    @Builder
    public VoucherKindImage(String imageKey, String imageUrl, String originalName) {
        super(imageKey, imageUrl, originalName);
    }
}
