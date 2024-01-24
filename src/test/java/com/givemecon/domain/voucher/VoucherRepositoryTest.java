package com.givemecon.domain.voucher;

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
    void saveAndFindAll() {
        // given
        String title = "Americano T";
        Long price = 15_000L;
        String image = "americano.jpg";

        Voucher voucher = Voucher.builder()
                .title(title)
                .price(price)
                .build();

        // when
        voucherRepository.save(voucher);
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        Voucher found = voucherList.get(0);
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getPrice()).isEqualTo(price);
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherRepository.save(Voucher.builder()
                .title("Cake")
                .price(10_000L)
                .build());

        // when
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        Voucher found = voucherList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}