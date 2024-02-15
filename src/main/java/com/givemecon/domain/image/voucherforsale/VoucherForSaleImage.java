package com.givemecon.domain.image.voucherforsale;

import com.givemecon.domain.image.ImageEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoucherForSaleImage extends ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder
    public VoucherForSaleImage(String imageKey, String imageUrl, String originalName) {
        super(imageKey, imageUrl, originalName);
    }
}
