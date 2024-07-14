package com.givemecon.domain.category.repository;


import com.givemecon.domain.category.entity.Category;
import com.givemecon.domain.image.entity.CategoryIcon;
import com.givemecon.domain.image.repository.CategoryIconRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        categoryRepository.save(Category.builder()
                .name("coffee")
                .build());

        // when
        List<Category> categoryList = categoryRepository.findAll();

        // then
        Category found = categoryList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Test
    void saveAndFindAll() {
        // given
        Category category = Category.builder()
                .name("coffee")
                .build();

        // when
        categoryRepository.save(category);
        List<Category> categoryList = categoryRepository.findAll();

        // then
        Category found = categoryList.get(0);
        assertThat(found).isEqualTo(category);
    }

    @Test
    @DisplayName("Category & CategoryIcon fetch join 조회 테스트")
    void findAllWithCategoryIcon(@Autowired CategoryIconRepository categoryIconRepository) {
        // given
        Category category = Category.builder()
                .name("coffee")
                .build();

        CategoryIcon categoryIcon = CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("coffeeIcon")
                .build();

        categoryIconRepository.save(categoryIcon);
        category.updateCategoryIcon(categoryIcon);
        categoryRepository.save(category);

        // when
        List<Category> found = categoryRepository.findAllWithCategoryIcon();

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0)).isEqualTo(category);
        assertThat(found.get(0).getCategoryIcon()).isEqualTo(categoryIcon);
    }
}