package com.givemecon.domain.entity.voucherkind;

import com.givemecon.domain.entity.ImageEntity;
import jakarta.persistence.Entity;
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
