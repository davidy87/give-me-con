package com.givemecon.domain.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class CategoryIconRepositoryTest {

    @Autowired
    CategoryIconRepository categoryIconRepository;

    @Test
    void saveAndFindAll() {
        // given
        String imageKey = "imageKey";
        String originalName = "testImage.png";
        CategoryIcon categoryIcon = CategoryIcon.builder()
                .imageKey(imageKey)
                .originalName(originalName)
                .build();

        // when
        categoryIconRepository.save(categoryIcon);
        List<CategoryIcon> categoryIconList = categoryIconRepository.findAll();

        // then
        CategoryIcon found = categoryIconList.get(0);
        assertThat(found.getImageKey()).isEqualTo(imageKey);
        assertThat(found.getOriginalName()).isEqualTo(originalName);
    }

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .originalName("testImage.png")
                .build());

        // when
        List<CategoryIcon> categoryIconList = categoryIconRepository.findAll();

        // then
        CategoryIcon found = categoryIconList.get(0);
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}