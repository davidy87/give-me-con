package com.givemecon.util.image_entity.concretebuilder;

import com.givemecon.domain.image.category.CategoryIcon;
import com.givemecon.util.image_entity.ImageEntityBuilder;
import org.springframework.stereotype.Component;

@Component
public class CategoryIconBuilder extends ImageEntityBuilder {

    @Override
    public Class<CategoryIcon> getEntityType() {
        return CategoryIcon.class;
    }

    @Override
    public CategoryIcon build(String imageKey, String imageUrl, String originalName) {
        return CategoryIcon.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .originalName(originalName)
                .build();
    }
}