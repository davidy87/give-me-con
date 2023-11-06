package com.givemecon.domain.brand;

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
class BrandRepositoryTest {

    @Autowired
    BrandRepository brandRepository;

    @Test
    void saveAndFindAll() {
        // given
        String name = "Starbucks";
        String icon = "starbucks.jpg";

        Brand brand = Brand.builder()
                .name(name)
                .icon(icon)
                .build();

        // when
        brandRepository.save(brand);
        List<Brand> brandList = brandRepository.findAll();

        // then
        Brand found = brandList.get(0);
        assertThat(found.getName()).isEqualTo(name);
        assertThat(found.getIcon()).isEqualTo(icon);
    }

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        brandRepository.save(Brand.builder()
                .name("BR31")
                .icon("BR31.jpg")
                .build());

        // when
        List<Brand> categoryList = brandRepository.findAll();

        // then
        Brand found = categoryList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}