package com.givemecon.infrastructure.s3.image_entity.builder.concrete;

import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.infrastructure.s3.image_entity.builder.ImageEntityBuilder;
import org.springframework.stereotype.Component;

@Component
public class VoucherKindImageBuilder extends ImageEntityBuilder {

    @Override
    public Class<VoucherKindImage> getEntityType() {
        return VoucherKindImage.class;
    }

    @Override
    public VoucherKindImage build(String imageKey, String imageUrl, String originalName) {
        return VoucherKindImage.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .originalName(originalName)
                .build();
    }
}
