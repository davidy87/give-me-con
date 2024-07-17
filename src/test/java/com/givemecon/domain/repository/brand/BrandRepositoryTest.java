package com.givemecon.domain.repository.brand;

import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class BrandRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryIconRepository categoryIconRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    BrandIconRepository brandIconRepository;

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
    void saveAndFindAll() {
        // given
        Brand brand = Brand.builder()
                .name("brand")
                .brandIcon(brandIcon)
                .category(category)
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
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        brandRepository.save(Brand.builder()
                .name("brand")
                .brandIcon(brandIcon)
                .category(category)
                .build());

        // when
        List<Brand> categoryList = brandRepository.findAll();

        // then
        Brand found = categoryList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Test
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