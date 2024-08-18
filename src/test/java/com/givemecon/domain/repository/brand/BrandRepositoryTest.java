package com.givemecon.domain.repository.brand;

import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BrandRepositoryTest extends RepositoryTest {

    @Test
    void saveAndFindAll() {
        // given
        Brand brand = Brand.builder()
                .name("brand")
                .build();

        // when
        brandRepository.save(brand);
        List<Brand> brandList = brandRepository.findAll();

        // then
        Brand found = brandList.get(0);
        assertThat(found).isEqualTo(brand);
        assertThat(found.getName()).isEqualTo(brand.getName());
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        brandRepository.save(Brand.builder()
                .name("brand")
                .build());

        // when
        List<Brand> categoryList = brandRepository.findAll();

        // then
        Brand found = categoryList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Nested
    @DisplayName("JPQL 테스트")
    class JPQLTest {

        Category category;

        BrandIcon brandIcon;

        @BeforeEach
        void setup() {
            CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                    .imageKey("imageKey")
                    .imageUrl("imageUrl")
                    .originalName("categoryIcon")
                    .build());

            category = categoryRepository.save(Category.builder()
                    .name("category")
                    .categoryIcon(categoryIcon)
                    .build());

            brandIcon = brandIconRepository.save(BrandIcon.builder()
                    .imageKey("imageKey")
                    .imageUrl("imageUrl")
                    .originalName("brandIcon")
                    .build());
        }

        @Test
        @DisplayName("Brand & BrandIcon fetch join 테스트")
        void findAllWithBrandIcon() {
            // given
            Brand brand = brandRepository.save(Brand.builder()
                    .name("Brand")
                    .brandIcon(brandIcon)
                    .category(category)
                    .build());

            // when
            List<Brand> found = brandRepository.findAllWithBrandIcon();

            // then
            assertThat(found).isNotEmpty();
            assertThat(found.get(0)).isEqualTo(brand);
            assertThat(found.get(0).getBrandIcon()).isEqualTo(brandIcon);
        }

        @Test
        @DisplayName("Brand & BrandIcon fetch join 후 categoryId 별로 조회하는 테스트")
        void findAllWithBrandIconByCategoryId() {
            // given
            Brand brand = brandRepository.save(Brand.builder()
                    .name("Brand")
                    .brandIcon(brandIcon)
                    .category(category)
                    .build());

            // when
            List<Brand> result = brandRepository.findAllWithBrandIconByCategoryId(category.getId());

            // then
            assertThat(result).isNotEmpty();

            Brand brandFound = result.get(0);
            assertThat(brandFound).isEqualTo(brand);
            assertThat(brandFound.getBrandIcon()).isEqualTo(brandIcon);
            assertThat(brandFound.getCategory()).isEqualTo(category);
        }
    }
}