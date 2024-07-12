package com.givemecon.domain.voucherkind;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.image.voucherkind.VoucherKindImage;
import com.givemecon.domain.image.voucherkind.VoucherKindImageRepository;
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
class VoucherKindRepositoryTest {

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherKindRepository.save(VoucherKind.builder()
                .title("Cake")
                .build());

        // when
        List<VoucherKind> voucherKindList = voucherKindRepository.findAll();

        // then
        VoucherKind found = voucherKindList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Test
    void saveAndFindAll() {
        // given
        String title = "Americano T";
        String description = "This is Americano T";
        String caution = "This is hot, not iced";

        VoucherKind voucherKind = VoucherKind.builder()
                .title(title)
                .description(description)
                .caution(caution)
                .build();

        // when
        voucherKindRepository.save(voucherKind);
        List<VoucherKind> voucherKindList = voucherKindRepository.findAll();

        // then
        VoucherKind found = voucherKindList.get(0);
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getDescription()).isEqualTo(description);
        assertThat(found.getCaution()).isEqualTo(caution);
    }

    @Test
    void findAllWithImageByBrandId(@Autowired BrandRepository brandRepository,
                                   @Autowired VoucherKindImageRepository voucherKindImageRepository) {

        // given
        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        VoucherKind voucherKind = VoucherKind.builder()
                .title("Americano T")
                .description("This is Americano T")
                .caution("This is hot, not iced")
                .build();

        VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        voucherKind.updateBrand(brand);
        voucherKind.updateVoucherImage(voucherKindImage);
        brandRepository.save(brand);
        voucherKindImageRepository.save(voucherKindImage);
        voucherKindRepository.save(voucherKind);

        // when
        List<VoucherKind> result = voucherKindRepository.findAllWithImageByBrandId(brand.getId());

        // then
        assertThat(result).isNotEmpty();

        VoucherKind voucherKindFound = result.get(0);
        assertThat(voucherKindFound).isEqualTo(voucherKind);
        assertThat(voucherKindFound.getBrand()).isEqualTo(brand);
        assertThat(voucherKindFound.getVoucherKindImage()).isEqualTo(voucherKindImage);
    }
}