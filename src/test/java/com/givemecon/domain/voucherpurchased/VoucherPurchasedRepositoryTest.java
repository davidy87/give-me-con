package com.givemecon.domain.voucherpurchased;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class VoucherPurchasedRepositoryTest {

    @Autowired
    VoucherPurchasedRepository voucherPurchasedRepository;

    @Test
    void saveAndFindAll() {
        // given
        String title = "voucher";
        Long price = 4_000L;
        LocalDate expDate = LocalDate.now();
        String barcode = "1111 1111 1111";
        String image = "voucher.png";

        VoucherPurchased voucherPurchased = VoucherPurchased.builder()
                .title(title)
                .price(price)
                .expDate(expDate)
                .barcode(barcode)
                .image(image)
                .build();

        // when
        voucherPurchasedRepository.save(voucherPurchased);
        List<VoucherPurchased> voucherPurchasedList = voucherPurchasedRepository.findAll();

        // then
        VoucherPurchased found = voucherPurchasedList.get(0);
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getPrice()).isEqualTo(price);
        assertThat(found.getExpDate()).isEqualTo(expDate);
        assertThat(found.getBarcode()).isEqualTo(barcode);
        assertThat(found.getImage()).isEqualTo(image);
    }

    @Test
    void BaseTimeEntity() {
        // given
        String title = "voucher";
        Long price = 4_000L;
        LocalDate expDate = LocalDate.now();
        String barcode = "1111 1111 1111";
        String image = "voucher.png";

        VoucherPurchased voucherPurchased = VoucherPurchased.builder()
                .title(title)
                .price(price)
                .expDate(expDate)
                .barcode(barcode)
                .image(image)
                .build();

        LocalDateTime now = LocalDateTime.now();
        voucherPurchasedRepository.save(voucherPurchased);

        // when
        List<VoucherPurchased> voucherPurchasedList = voucherPurchasedRepository.findAll();

        // then
        VoucherPurchased found = voucherPurchasedList.get(0);
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}