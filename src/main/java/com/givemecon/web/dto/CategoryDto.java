package com.givemecon.web.dto;

import com.givemecon.domain.category.Category;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class CategoryDto {

    @Getter
    @Builder
    public static class CategorySaveRequest {

        private String name;

        private MultipartFile icon;

        public Category toEntity() {
            return Category.builder()
                    .name(name)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CategoryUpdateRequest {

        private String name;

        private MultipartFile icon;
    }

    @Getter
    public static class CategoryResponse {

        private Long id;

        private String name;

        private String icon;

        public CategoryResponse(Category category) {
            this.id = category.getId();
            this.name = category.getName();
            this.icon = category.getCategoryIcon().getImageUrl();
        }
    }
}
