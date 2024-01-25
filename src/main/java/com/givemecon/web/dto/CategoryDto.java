package com.givemecon.web.dto;

import com.givemecon.domain.category.Category;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class CategoryDto {

    @Getter
    @Builder
    public static class CategorySaveRequest {

        private final String name;

        private final MultipartFile icon;

        public Category toEntity() {
            return Category.builder()
                    .name(name)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CategoryUpdateRequest {

        private final String name;

        private final MultipartFile icon;
    }

    @Getter
    public static class CategoryResponse {

        private final Long id;

        private final String name;

        private final String icon;

        public CategoryResponse(Category category) {
            this.id = category.getId();
            this.name = category.getName();
            this.icon = category.getCategoryIcon().getImageUrl();
        }
    }
}
