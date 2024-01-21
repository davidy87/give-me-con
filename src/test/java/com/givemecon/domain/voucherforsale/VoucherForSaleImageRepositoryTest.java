package com.givemecon.domain.voucherforsale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class VoucherForSaleImageRepositoryTest {

    @Autowired
    VoucherForSaleImageRepository voucherForSaleImageRepository;

    @Test
    void saveAndFindAll() {
        // given
        String imageUrl = "imageUrl";
        String imageKey = "imageKey";
        String originalName = "voucherImage.png";

        VoucherForSaleImage voucherForSaleImage = VoucherForSaleImage.builder()
                .imageUrl(imageUrl)
                .imageKey(imageKey)
                .originalName(originalName)
                .build();

        // when
        voucherForSaleImageRepository.save(voucherForSaleImage);
        List<VoucherForSaleImage> voucherForSaleImageList = voucherForSaleImageRepository.findAll();

        // then
        VoucherForSaleImage found = voucherForSaleImageList.get(0);
        assertThat(found.getImageUrl()).isEqualTo(imageUrl);
        assertThat(found.getImageKey()).isEqualTo(imageKey);
        assertThat(found.getOriginalName()).isEqualTo(originalName);
    }

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                .imageKey("imageKey")
                .originalName("testImage.png")
                .imageUrl("imageUrl")
                .build());

        // when
        List<VoucherForSaleImage> voucherImageList = voucherForSaleImageRepository.findAll();

        // then
        VoucherForSaleImage found = voucherImageList.get(0);
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}