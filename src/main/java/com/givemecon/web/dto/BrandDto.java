package com.givemecon.web.dto;

import com.givemecon.domain.brand.Brand;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class BrandDto {

    @Getter
    @Builder
    public static class BrandSaveRequest {

        private final Long categoryId;

        private final String name;

        private final MultipartFile icon;

        public Brand toEntity() {
            return Brand.builder()
                    .name(name)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BrandUpdateRequest {

        private final Long categoryId;

        private final String name;

        private final MultipartFile icon;
    }

    @Getter
    public static class BrandResponse {

        private final Long id;

        private final String name;

        private final String icon;

        public BrandResponse(Brand brand) {
            this.id = brand.getId();
            this.name = brand.getName();
            this.icon = brand.getBrandIcon().getImageUrl();
        }
    }
}
