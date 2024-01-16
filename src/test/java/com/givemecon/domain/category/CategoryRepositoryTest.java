package com.givemecon.domain.category;


import lombok.extern.slf4j.Slf4j;
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
    void saveAndFindAll() {
        // given
        String name = "coffee";
        String icon = "coffee.jpg";

        Category category = Category.builder()
                .name(name)
                .build();

        // when
        categoryRepository.save(category);
        List<Category> categoryList = categoryRepository.findAll();

        // then
        Category found = categoryList.get(0);
        assertThat(found.getName()).isEqualTo(name);
    }

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
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}