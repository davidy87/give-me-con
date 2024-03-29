package com.givemecon.web.dto;

import com.givemecon.domain.brand.Brand;
import com.givemecon.util.validator.ValidFile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class BrandDto {

    @Getter
    @Builder
    public static class BrandSaveRequest {

        @NotNull
        @Min(1)
        private final Long categoryId;

        @NotBlank
        private final String name;

        @ValidFile
        private final MultipartFile iconFile;

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

        private final MultipartFile iconFile;
    }

    @Getter
    public static class BrandResponse {

        private final Long id;

        private final String name;

        private final String iconUrl;

        public BrandResponse(Brand brand) {
            this.id = brand.getId();
            this.name = brand.getName();
            this.iconUrl = brand.getImageUrl();
        }
    }
}
