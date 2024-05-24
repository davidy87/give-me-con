package com.givemecon.domain.purchasedvoucher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus.USED;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class PurchasedVoucherRepositoryTest {

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Test
    void saveAndFindAll() {
        // given
        PurchasedVoucher purchasedVoucher = new PurchasedVoucher();

        // when
        purchasedVoucherRepository.save(purchasedVoucher);
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getStatus()).isEqualTo(USABLE);
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        PurchasedVoucher purchasedVoucher = new PurchasedVoucher();
        purchasedVoucherRepository.save(purchasedVoucher);

        // when
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }

    @Test
    @DisplayName("PurchasedVoucher 쿼리 시, 'deleted = false and status like USABLE' 조건이 붙는다.")
    void sqlRestrictionTest() {
        // given
        List<PurchasedVoucher> toSaveList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            toSaveList.add(new PurchasedVoucher());
        }

        List<PurchasedVoucher> savedPurchasedVouchers = purchasedVoucherRepository.saveAll(toSaveList);

        // when
        savedPurchasedVouchers.forEach(purchasedVoucher -> purchasedVoucher.updateStatus(USED));

        // then
        List<PurchasedVoucher> foundPurchasedVouchers = purchasedVoucherRepository.findAll();
        assertThat(foundPurchasedVouchers).isEmpty();
    }
}