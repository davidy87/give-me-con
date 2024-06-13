package com.givemecon.domain.brand;

import com.givemecon.domain.image.brand.BrandIcon;
import com.givemecon.domain.image.brand.BrandIconRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class BrandIconRepositoryTest {

    @Autowired
    BrandIconRepository brandIconRepository;

    @Test
    void saveAndFindAll() {
        // given
        String imageKey = UUID.randomUUID() + ".png";
        String originalName = "testImage.png";
        String imageUrl = "imageUrl";
        BrandIcon brandIcon = BrandIcon.builder()
                .imageKey(imageKey)
                .originalName(originalName)
                .imageUrl(imageUrl)
                .build();

        // when
        brandIconRepository.save(brandIcon);
        List<BrandIcon> brandIconList = brandIconRepository.findAll();

        // then
        BrandIcon found = brandIconList.get(0);
        assertThat(found.getImageKey()).isEqualTo(imageKey);
        assertThat(found.getOriginalName()).isEqualTo(originalName);
        assertThat(found.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        brandIconRepository.save(BrandIcon.builder()
                .imageKey("imageKey")
                .originalName("testImage.png")
                .imageUrl("imageUrl")
                .build());

        // when
        List<BrandIcon> brandIconList = brandIconRepository.findAll();

        // then
        BrandIcon found = brandIconList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }
}