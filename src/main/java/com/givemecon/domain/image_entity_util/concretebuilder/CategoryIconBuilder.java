package com.givemecon.domain.image_entity_util.concretebuilder;

import com.givemecon.domain.category.CategoryIcon;
import com.givemecon.domain.image_entity_util.ImageEntityBuilder;
import org.springframework.stereotype.Component;

@Component
public class CategoryIconBuilder extends ImageEntityBuilder<CategoryIcon> {

    @Override
    public String getOwnerEntityName() {
        return "Category"; // TODO: 수정 필요
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
