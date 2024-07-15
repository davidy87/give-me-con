package com.givemecon.util.image_entity.builder.concrete;

import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.util.image_entity.builder.ImageEntityBuilder;
import org.springframework.stereotype.Component;

@Component
public class BrandIconBuilder extends ImageEntityBuilder {

    @Override
    public Class<BrandIcon> getEntityType() {
        return BrandIcon.class;
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
