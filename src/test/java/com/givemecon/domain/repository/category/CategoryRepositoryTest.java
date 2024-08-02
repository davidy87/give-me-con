package com.givemecon.domain.repository.category;

import com.givemecon.common.configuration.JpaConfig;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Import(JpaConfig.class)
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void saveAndFindAll() {
        // given
        Category category =  Category.builder()
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
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Category category =  Category.builder()
                .name("coffee")
                .build();

        categoryRepository.save(category);

        // when
        List<Category> categoryList = categoryRepository.findAll();

        // then
        Category found = categoryList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Nested
    @DisplayName("JPQL 테스트")
    class JPQLTest {

        @Autowired
        CategoryIconRepository categoryIconRepository;

        @Test
        @DisplayName("Category & CategoryIcon fetch join 조회 테스트")
        void findAllWithCategoryIcon() {
            // given
            CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                    .imageKey("imageKey")
                    .imageUrl("imageUrl")
                    .originalName("categoryIcon")
                    .build());

            Category category = Category.builder()
                    .name("coffee")
                    .categoryIcon(categoryIcon)
                    .build();

            categoryRepository.save(category);

            // when
            List<Category> found = categoryRepository.findAllWithCategoryIcon();

            // then
            assertThat(found).isNotEmpty();
            assertThat(found.get(0)).isEqualTo(category);
            assertThat(found.get(0).getCategoryIcon()).isEqualTo(categoryIcon);
        }
    }
}