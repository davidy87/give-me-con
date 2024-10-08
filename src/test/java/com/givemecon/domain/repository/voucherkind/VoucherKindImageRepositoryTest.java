package com.givemecon.domain.repository.voucherkind;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VoucherKindImageRepositoryTest extends IntegrationTestEnvironment {

    @Test
    void saveAndFindAll() {
        // given
        String imageUrl = "imageUrl";
        String imageKey = "imageKey";
        String originalName = "voucherKindImage.png";

        VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                .imageUrl(imageUrl)
                .imageKey(imageKey)
                .originalName(originalName)
                .build();

        // when
        voucherKindImageRepository.save(voucherKindImage);
        List<VoucherKindImage> voucherKindImageList = voucherKindImageRepository.findAll();

        // then
        VoucherKindImage found = voucherKindImageList.get(0);
        assertThat(found.getImageUrl()).isEqualTo(imageUrl);
        assertThat(found.getImageKey()).isEqualTo(imageKey);
        assertThat(found.getOriginalName()).isEqualTo(originalName);
    }

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .originalName("testImage.png")
                .imageUrl("imageUrl")
                .build());

        // when
        List<VoucherKindImage> voucherKindImageList = voucherKindImageRepository.findAll();

        // then
        VoucherKindImage found = voucherKindImageList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }
}