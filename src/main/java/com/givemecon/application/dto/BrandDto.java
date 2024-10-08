package com.givemecon.application.dto;

import com.givemecon.application.dto.validator.ValidFile;
import com.givemecon.domain.entity.brand.Brand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BrandDto {

    @Getter
    @RequiredArgsConstructor
    public static class BrandSaveRequest {

        @NotNull
        @Min(1)
        private final Long categoryId;

        @NotBlank
        private final String name;

        @ValidFile
        private final MultipartFile iconFile;
    }

    @Getter
    @RequiredArgsConstructor
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

    @Getter
    public static class PagedBrandResponse {

        private final int number;

        private final int totalPages;

        private final int size;

        private final List<BrandResponse> brands;

        public PagedBrandResponse(Page<BrandResponse> pageResult) {
            this.number = pageResult.getNumber();
            this.totalPages = pageResult.getTotalPages();
            this.size = pageResult.getSize();
            this.brands = pageResult.getContent();
        }
    }
}
