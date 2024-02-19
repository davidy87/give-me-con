package com.givemecon.util.image_entity.builder.concrete;

import com.givemecon.util.image_entity.builder.ImageEntityBuilder;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import org.springframework.stereotype.Component;

@Component
public class VoucherForSaleImageBuilder extends ImageEntityBuilder {

    @Override
    public Class<VoucherForSaleImage> getEntityType() {
        return VoucherForSaleImage.class;
    }

    @Override
    public VoucherForSaleImage build(String imageKey, String imageUrl, String originalName) {
        return VoucherForSaleImage.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .originalName(originalName)
                .build();
    }
}
