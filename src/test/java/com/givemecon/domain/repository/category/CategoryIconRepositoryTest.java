package com.givemecon.domain.repository.category;

import com.givemecon.common.configuration.JpaConfig;
import com.givemecon.domain.entity.category.CategoryIcon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Import(JpaConfig.class)
@DataJpaTest
class CategoryIconRepositoryTest {

    @Autowired
    CategoryIconRepository categoryIconRepository;

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