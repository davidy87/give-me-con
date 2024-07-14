package com.givemecon.util.image_entity.builder.concrete;

import com.givemecon.domain.image.entity.VoucherKindImage;
import com.givemecon.util.image_entity.builder.ImageEntityBuilder;
import org.springframework.stereotype.Component;

@Component
public class VoucherImageBuilder extends ImageEntityBuilder {

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
