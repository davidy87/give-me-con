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
        Long price = 15_000L;
        String image = "americano.jpg";

        Voucher voucher = Voucher.builder()
                .price(price)
                .image(image)
                .build();

        // when
        voucherRepository.save(voucher);
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        Voucher found = voucherList.get(0);
        assertThat(found.getPrice()).isEqualTo(price);
        assertThat(found.getImage()).isEqualTo(image);
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherRepository.save(Voucher.builder()
                .price(10_000L)
                .image("cake.jpg")
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