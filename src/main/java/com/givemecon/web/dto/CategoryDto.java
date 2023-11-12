package com.givemecon.web.dto;

import com.givemecon.domain.category.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.givemecon.web.dto.BrandDto.*;

public class CategoryDto {

    @Getter
    @Builder
    public static class CategorySaveRequest {

        private String name;

        private String icon;

        public Category toEntity() {
            return Category.builder()
                    .name(name)
                    .icon(icon)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CategoryUpdateRequest {

        private String name;

        private String icon;
    }

    @Getter
    @Builder
    public static class CategoryResponse {

        private Long id;

        private String name;

        private String icon;

        private List<BrandResponse> brands;
    }
}
