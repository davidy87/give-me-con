package com.givemecon.web.dto;

import com.givemecon.domain.category.Category;
import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class CategoryDto {

    @Getter
    @Builder
    public static class CategorySaveRequest {

        @NotBlank
        private final String name;

        @ValidFile
        private final MultipartFile iconFile;

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

        private final MultipartFile iconFile;
    }

    @Getter
    public static class CategoryResponse {

        private final Long id;

        private final String name;

        private final String iconUrl;

        public CategoryResponse(Category category) {
            this.id = category.getId();
            this.name = category.getName();
            this.iconUrl = category.getCategoryIcon().getImageUrl();
        }
    }
}
