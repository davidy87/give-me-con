package com.givemecon.web.dto;

import com.givemecon.domain.brand.Brand;
import lombok.Builder;
import lombok.Getter;

public class BrandDto {

    @Getter
    @Builder
    public static class BrandSaveRequest {

        private String name;

        private String icon;

        public Brand toEntity() {
            return Brand.builder()
                    .name(name)
                    .icon(icon)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class BrandUpdateRequest {

        private String name;

        private String icon;
    }

    @Getter
    @Builder
    public static class BrandResponse {

        private String name;

        private String icon;
    }
}
