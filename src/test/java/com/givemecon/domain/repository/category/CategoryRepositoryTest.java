package com.givemecon.domain.repository.category;

import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.RepositoryTestEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRepositoryTest extends RepositoryTestEnvironment {

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
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Nested
    @DisplayName("JPQL 테스트")
    class JPQLTest {

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