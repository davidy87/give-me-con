package com.givemecon.domain.image_entity_util.concretebuilder;

import com.givemecon.domain.brand.BrandIcon;
import com.givemecon.domain.image_entity_util.ImageEntityBuilder;
import org.springframework.stereotype.Component;

@Component
public class BrandIconBuilder extends ImageEntityBuilder<BrandIcon> {

    @Override
    public String getOwnerEntityName() {
        return "Brand"; // TODO: 수정 필요
    }

    @Override
    public BrandIcon build(String imageKey, String imageUrl, String originalName) {
        return BrandIcon.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .originalName(originalName)
                .build();
    }
}
