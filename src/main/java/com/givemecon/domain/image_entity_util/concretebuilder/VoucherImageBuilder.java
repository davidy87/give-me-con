package com.givemecon.domain.image_entity_util.concretebuilder;

import com.givemecon.domain.image_entity_util.ImageEntityBuilder;
import com.givemecon.domain.voucher.VoucherImage;
import org.springframework.stereotype.Component;

@Component
public class VoucherImageBuilder extends ImageEntityBuilder {

    @Override
    public String getOwnerEntityName() {
        return "Voucher"; // TODO: 수정 필요
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
