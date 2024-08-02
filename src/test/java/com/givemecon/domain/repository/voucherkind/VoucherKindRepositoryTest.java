package com.givemecon.domain.repository.voucherkind;

import com.givemecon.common.configuration.JpaConfig;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.domain.repository.brand.BrandRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Import(JpaConfig.class)
@DataJpaTest
class VoucherKindRepositoryTest {

    @Autowired
    VoucherKindRepository voucherKindRepository;

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
    void findAllWithImageByBrandId(@Autowired BrandRepository brandRepository,
                                   @Autowired VoucherKindImageRepository voucherKindImageRepository) {

        // given
        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());

        VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        VoucherKind voucherKind = VoucherKind.builder()
                .title("Americano T")
                .description("This is Americano T")
                .caution("This is hot, not iced")
                .voucherKindImage(voucherKindImage)
                .brand(brand)
                .build();

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