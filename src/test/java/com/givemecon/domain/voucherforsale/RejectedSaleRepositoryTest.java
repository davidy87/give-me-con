package com.givemecon.domain.voucherforsale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class RejectedSaleRepositoryTest {

    @Autowired
    RejectedSaleRepository rejectedSaleRepository;

    @Test
    void save() {
        // given
        Long voucherForSaleId = 1L;
        String rejectedReason = "유효기간 만료";
        RejectedSale rejectedSale = RejectedSale.builder()
                .voucherForSaleId(voucherForSaleId)
                .rejectedReason(rejectedReason)
                .build();

        // when
        RejectedSale saved = rejectedSaleRepository.save(rejectedSale);

        // then
        List<RejectedSale> rejectedSaleList = rejectedSaleRepository.findAll();
        assertThat(rejectedSaleList).isNotEmpty();

        RejectedSale found = rejectedSaleList.get(0);
        assertThat(found).isEqualTo(saved);
        assertThat(found.getVoucherForSaleId()).isEqualTo(voucherForSaleId);
        assertThat(found.getRejectedReason()).isEqualTo(rejectedReason);
    }

    @Test
    void delete() {
        // given
        Long voucherForSaleId = 1L;
        String rejectedReason = "유효기간 만료";
        RejectedSale rejectedSale = rejectedSaleRepository.save(RejectedSale.builder()
                .voucherForSaleId(voucherForSaleId)
                .rejectedReason(rejectedReason)
                .build());

        // when
        rejectedSaleRepository.delete(rejectedSale);

        // then
        List<RejectedSale> rejectedSaleList = rejectedSaleRepository.findAll();
        assertThat(rejectedSaleList).isEmpty();
    }
}