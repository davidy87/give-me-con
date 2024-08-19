package com.givemecon.domain.repository.voucher;

import com.givemecon.domain.entity.voucher.RejectedSale;
import com.givemecon.domain.repository.RepositoryTestEnvironment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RejectedSaleRepositoryTest extends RepositoryTestEnvironment {

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