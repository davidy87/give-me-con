package com.givemecon.domain.category;

import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryDto {

    @Getter
    @RequiredArgsConstructor
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
    @RequiredArgsConstructor
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
            this.iconUrl = category.getImageUrl();
        }
    }
}
