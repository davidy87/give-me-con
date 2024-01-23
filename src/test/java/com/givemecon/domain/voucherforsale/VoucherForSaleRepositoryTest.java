package com.givemecon.domain.voucherforsale;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class VoucherForSaleRepositoryTest {

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Test
    void saveAndFindAll() {
        // given
        String title = "Starbucks Americano T";
        Long price = 15_000L;
        LocalDate expDate = LocalDate.now();
        String barcode = "1111 1111 1111";

        VoucherForSale voucher = VoucherForSale.builder()
                .title(title)
                .price(price)
                .expDate(expDate)
                .barcode(barcode)
                .build();

        // when
        voucherForSaleRepository.save(voucher);
        List<VoucherForSale> voucherList = voucherForSaleRepository.findAll();

        // then
        VoucherForSale found = voucherList.get(0);
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getPrice()).isEqualTo(price);
        assertThat(found.getExpDate()).isEqualTo(expDate);
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherForSaleRepository.save(VoucherForSale.builder()
                .title("Cake")
                .price(10_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build());

        // when
        List<VoucherForSale> voucherList = voucherForSaleRepository.findAll();

        // then
        VoucherForSale found = voucherList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}