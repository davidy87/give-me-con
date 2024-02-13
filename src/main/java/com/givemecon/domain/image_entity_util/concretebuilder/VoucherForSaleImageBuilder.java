package com.givemecon.domain.image_entity_util.concretebuilder;

import com.givemecon.domain.image_entity_util.ImageEntityBuilder;
import com.givemecon.domain.voucherforsale.VoucherForSaleImage;
import org.springframework.stereotype.Component;

@Component
public class VoucherForSaleImageBuilder extends ImageEntityBuilder {

    @Override
    public String getOwnerEntityName() {
        return "VoucherForSale"; // TODO: 수정 필요
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
