package com.givemecon.domain.repository.brand;

import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
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
    BrandRepository brandRepository;

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        brandRepository.save(Brand.builder()
                .name("BR31")
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
    void saveAndFindAll() {
        // given
        Brand brand = Brand.builder()
                .name("Starbucks")
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
    void findAllWithBrandIcon(@Autowired BrandIconRepository brandIconRepository) {
        // given
        Brand brand = Brand.builder()
                .name("Brand")
                .build();

        BrandIcon brandIcon = BrandIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("coffeeIcon")
                .build();

        brandIconRepository.save(brandIcon);
        brand.updateBrandIcon(brandIcon);
        brandRepository.save(brand);

        // when
        List<Brand> found = brandRepository.findAllWithBrandIcon();

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0)).isEqualTo(brand);
        assertThat(found.get(0).getBrandIcon()).isEqualTo(brandIcon);
    }

    @Test
    void findAllWithBrandIconByCategoryId(@Autowired CategoryRepository categoryRepository,
                                          @Autowired CategoryIconRepository categoryIconRepository,
                                          @Autowired BrandIconRepository brandIconRepository) {

        // given
        Category category = Category.builder()
                .name("category")
                .build();

        CategoryIcon categoryIcon = CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        category.updateCategoryIcon(categoryIcon);
        categoryIconRepository.save(categoryIcon);
        categoryRepository.save(category);

        Brand brand = Brand.builder()
                .name("Brand")
                .build();

        BrandIcon brandIcon = BrandIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("coffeeIcon")
                .build();

        brandIconRepository.save(brandIcon);
        brand.updateBrandIcon(brandIcon);
        brand.updateCategory(category);
        brandRepository.save(brand);

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