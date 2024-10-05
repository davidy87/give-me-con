package com.givemecon.event.scheduler;

import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.repository.PurchasedVoucherRepository;
import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucherStatus;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.entity.voucher.VoucherStatus;
import com.givemecon.event.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Mock
    VoucherRepository voucherRepository;

    @Mock
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Mock
    NotificationRepository notificationRepository;

    @InjectMocks
    SchedulerService schedulerService;

    @Test
    @DisplayName("유효기간이 만료된 모든 Voucher과 PurchasedVoucher의 status를 변경한다.")
    void updateExpired() {
        // given
        LocalDate today = LocalDate.now();
        List<Voucher> voucherList = new ArrayList<>();
        List<PurchasedVoucher> purchasedVoucherList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Voucher voucher = Voucher.builder()
                    .price(4_000L)
                    .expDate(today.minusDays(1))
                    .barcode("1111 1111 1111")
                    .build();

            PurchasedVoucher purchasedVoucher = new PurchasedVoucher(voucher, Member.builder().build());
            voucherList.add(voucher);
            purchasedVoucherList.add(purchasedVoucher);
        }

        Mockito.when(voucherRepository.updateAllByExpDateBefore(any(LocalDate.class)))
                .then(invocation -> {
                    voucherList.forEach(v -> v.updateStatus(VoucherStatus.EXPIRED));
                    return voucherList.size();
                });

        Mockito.when(purchasedVoucherRepository.updateAllStatusForExpired())
                .then(invocation -> {
                    purchasedVoucherList.forEach(pv -> pv.updateStatus(PurchasedVoucherStatus.EXPIRED));
                    return purchasedVoucherList.size();
                });

        // when
        schedulerService.updateExpired(today);

        // then
        voucherList.forEach(voucherForSale ->
                assertThat(voucherForSale.getStatus()).isEqualTo(VoucherStatus.EXPIRED));

        purchasedVoucherList.forEach(purchasedVoucher ->
                assertThat(purchasedVoucher.getStatus()).isEqualTo(PurchasedVoucherStatus.EXPIRED));
    }

    @Test
    @DisplayName("생성된지 30일이 지난 모든 Notification을 삭제한다.")
    void deleteNotificationsThirtyDaysOld() {
        // given
        LocalDate now = LocalDate.now();

        // when
        schedulerService.deleteNotificationsThirtyDaysOld(now);

        // then
        Mockito.verify(notificationRepository)
                .deleteAllByCreatedDateBefore(now.atStartOfDay().minusDays(30));
    }
}