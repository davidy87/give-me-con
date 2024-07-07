package com.givemecon.domain.voucher;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.image.voucher.VoucherImageRepository;
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
class VoucherRepositoryTest {

    @Autowired
    VoucherRepository voucherRepository;

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherRepository.save(Voucher.builder()
                .title("Cake")
                .build());

        // when
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        Voucher found = voucherList.get(0);
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

        Voucher voucher = Voucher.builder()
                .title(title)
                .description(description)
                .caution(caution)
                .build();

        // when
        voucherRepository.save(voucher);
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        Voucher found = voucherList.get(0);
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getDescription()).isEqualTo(description);
        assertThat(found.getCaution()).isEqualTo(caution);
    }

    @Test
    void findAllWithVoucherImageByBrandId(@Autowired BrandRepository brandRepository,
                                          @Autowired VoucherImageRepository voucherImageRepository) {

        // given
        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        Voucher voucher = Voucher.builder()
                .title("Americano T")
                .description("This is Americano T")
                .caution("This is hot, not iced")
                .build();

        VoucherImage voucherImage = VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        voucher.updateBrand(brand);
        voucher.updateVoucherImage(voucherImage);
        brandRepository.save(brand);
        voucherImageRepository.save(voucherImage);
        voucherRepository.save(voucher);

        // when
//        List<Voucher> result = voucherRepository.findAllWithVoucherImageByBrandName(brand.getName());

        List<Voucher> result = voucherRepository.findAllWithVoucherImageByBrandId(brand.getId());

        // then
        assertThat(result).isNotEmpty();

        Voucher voucherFound = result.get(0);
        assertThat(voucherFound).isEqualTo(voucher);
        assertThat(voucherFound.getBrand()).isEqualTo(brand);
        assertThat(voucherFound.getVoucherImage()).isEqualTo(voucherImage);
    }
}