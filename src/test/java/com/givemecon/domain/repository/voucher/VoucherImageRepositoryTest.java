package com.givemecon.domain.repository.voucher;

import com.givemecon.domain.entity.voucher.VoucherImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class VoucherImageRepositoryTest {

    @Autowired
    VoucherImageRepository voucherImageRepository;

    @Test
    void saveAndFindAll() {
        // given
        String imageUrl = "imageUrl";
        String imageKey = "imageKey";
        String originalName = "voucherImage.png";

        VoucherImage voucherImage = VoucherImage.builder()
                .imageUrl(imageUrl)
                .imageKey(imageKey)
                .originalName(originalName)
                .build();

        // when
        voucherImageRepository.save(voucherImage);
        List<VoucherImage> voucherImageList = voucherImageRepository.findAll();

        // then
        VoucherImage found = voucherImageList.get(0);
        assertThat(found.getImageUrl()).isEqualTo(imageUrl);
        assertThat(found.getImageKey()).isEqualTo(imageKey);
        assertThat(found.getOriginalName()).isEqualTo(originalName);
    }

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .originalName("testImage.png")
                .imageUrl("imageUrl")
                .build());

        // when
        List<VoucherImage> voucherImageList = voucherImageRepository.findAll();

        // then
        VoucherImage found = voucherImageList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }
}