package com.givemecon.domain.repository.voucherkind;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VoucherKindRepositoryTest extends IntegrationTestEnvironment {

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
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Nested
    @DisplayName("JPQL 테스트")
    class JPQLTest {

        @Test
        void findAllWithImageByBrandId() {
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
}