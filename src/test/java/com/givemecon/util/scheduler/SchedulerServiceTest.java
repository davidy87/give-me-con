package com.givemecon.util.scheduler;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.domain.voucherforsale.VoucherForSaleStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@Transactional
@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Mock
    VoucherForSaleRepository voucherForSaleRepository;

    @Mock
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Test
    @DisplayName("유효기간이 만료된 모든 VoucherForSale과 PurchasedVoucher의 status를 변경한다.")
    void updateExpired() {
        // given
        LocalDate today = LocalDate.now();
        List<VoucherForSale> voucherForSaleList = new ArrayList<>();
        List<PurchasedVoucher> purchasedVoucherList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            VoucherForSale voucherForSale = VoucherForSale.builder()
                    .price(4_000L)
                    .expDate(today.minusDays(1))
                    .barcode("1111 1111 1111")
                    .build();

            PurchasedVoucher purchasedVoucher = new PurchasedVoucher(voucherForSale, Member.builder().build());
            voucherForSaleList.add(voucherForSale);
            purchasedVoucherList.add(purchasedVoucher);
        }

        Mockito.when(voucherForSaleRepository.updateAllByExpDateBefore(any(LocalDate.class)))
                .then(invocation -> {
                    voucherForSaleList.forEach(vfs -> vfs.updateStatus(VoucherForSaleStatus.EXPIRED));
                    return voucherForSaleList.size();
                });

        Mockito.when(purchasedVoucherRepository.updateAllStatusForExpired())
                .then(invocation -> {
                    purchasedVoucherList.forEach(pv -> pv.updateStatus(PurchasedVoucherStatus.EXPIRED));
                    return purchasedVoucherList.size();
                });

        // when
        SchedulerService schedulerService = new SchedulerService(voucherForSaleRepository, purchasedVoucherRepository);
        schedulerService.updateExpired(today);

        // then
        voucherForSaleList.forEach(voucherForSale ->
                assertThat(voucherForSale.getStatus()).isEqualTo(VoucherForSaleStatus.EXPIRED));

        purchasedVoucherList.forEach(purchasedVoucher ->
                assertThat(purchasedVoucher.getStatus()).isEqualTo(PurchasedVoucherStatus.EXPIRED));
    }
}