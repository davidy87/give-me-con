package com.givemecon.infrastructure.s3.image_entity.builder.concrete;

import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.infrastructure.s3.image_entity.builder.ImageEntityBuilder;
import org.springframework.stereotype.Component;

@Component
public class VoucherImageBuilder extends ImageEntityBuilder {

    @Override
    public Class<VoucherImage> getEntityType() {
        return VoucherImage.class;
    }

    @Override
    public VoucherImage build(String imageKey, String imageUrl, String originalName) {
        return VoucherImage.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .originalName(originalName)
                .build();
    }
}
