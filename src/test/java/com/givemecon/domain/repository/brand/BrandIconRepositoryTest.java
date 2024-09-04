package com.givemecon.domain.repository.brand;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.domain.entity.brand.BrandIcon;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BrandIconRepositoryTest extends IntegrationTestEnvironment {

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