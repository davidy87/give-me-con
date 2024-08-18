package com.givemecon.domain.repository.category;

import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.RepositoryTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryIconRepositoryTest extends RepositoryTest {

    @Test
    void saveAndFindAll() {
        // given
        String imageKey = "imageKey";
        String originalName = "testImage.png";
        String imageUrl = "imageUrl";
        CategoryIcon categoryIcon = CategoryIcon.builder()
                .imageKey(imageKey)
                .originalName(originalName)
                .imageUrl(imageUrl)
                .build();

        // when
        categoryIconRepository.save(categoryIcon);
        List<CategoryIcon> categoryIconList = categoryIconRepository.findAll();

        // then
        CategoryIcon found = categoryIconList.get(0);
        assertThat(found.getImageKey()).isEqualTo(imageKey);
        assertThat(found.getOriginalName()).isEqualTo(originalName);
        assertThat(found.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .originalName("testImage.png")
                .imageUrl("imageUrl")
                .build());

        // when
        List<CategoryIcon> categoryIconList = categoryIconRepository.findAll();

        // then
        CategoryIcon found = categoryIconList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }
}