package com.givemecon.domain.voucher;

import com.givemecon.domain.ImageEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoucherImage extends ImageEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Builder
    public VoucherImage(String imageKey, String imageUrl, String originalName) {
        super(imageKey, imageUrl, originalName);
    }
}
