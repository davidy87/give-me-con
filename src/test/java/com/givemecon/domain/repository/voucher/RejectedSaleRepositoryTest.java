package com.givemecon.domain.repository.voucher;

import com.givemecon.common.configuration.JpaConfig;
import com.givemecon.domain.entity.voucher.RejectedSale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaConfig.class)
@DataJpaTest
class RejectedSaleRepositoryTest {

    @Autowired
    RejectedSaleRepository rejectedSaleRepository;

    @Test
    void save() {
        // given
        Long voucherId = 1L;
        String rejectedReason = "유효기간 만료";
        RejectedSale rejectedSale = RejectedSale.builder()
                .voucherId(voucherId)
                .rejectedReason(rejectedReason)
                .build();

        // when
        RejectedSale saved = rejectedSaleRepository.save(rejectedSale);

        // then
        List<RejectedSale> rejectedSaleList = rejectedSaleRepository.findAll();
        assertThat(rejectedSaleList).isNotEmpty();

        RejectedSale found = rejectedSaleList.get(0);
        assertThat(found).isEqualTo(saved);
        assertThat(found.getVoucherId()).isEqualTo(voucherId);
        assertThat(found.getRejectedReason()).isEqualTo(rejectedReason);
    }

    @Test
    void delete() {
        // given
        Long voucherId = 1L;
        String rejectedReason = "유효기간 만료";
        RejectedSale rejectedSale = rejectedSaleRepository.save(RejectedSale.builder()
                .voucherId(voucherId)
                .rejectedReason(rejectedReason)
                .build());

        // when
        rejectedSaleRepository.delete(rejectedSale);

        // then
        List<RejectedSale> rejectedSaleList = rejectedSaleRepository.findAll();
        assertThat(rejectedSaleList).isEmpty();
    }
}