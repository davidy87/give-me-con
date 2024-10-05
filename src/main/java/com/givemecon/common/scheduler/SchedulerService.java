package com.givemecon.common.scheduler;

import com.givemecon.domain.repository.PurchasedVoucherRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.common.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional
public class SchedulerService {

    private final VoucherRepository voucherRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    private final NotificationRepository notificationRepository;

    public void updateExpired(LocalDate today) {
        voucherRepository.updateAllByExpDateBefore(today);
        purchasedVoucherRepository.updateAllStatusForExpired();
    }

    /**
     * 생성된지 30일이 지난 알림을 모두 삭제하는 쿼리 호출
     */
    public void deleteNotificationsThirtyDaysOld(LocalDate today) {
        LocalDateTime thirtyDaysAgo = today.atStartOfDay().minusDays(30);
        notificationRepository.deleteAllByCreatedDateBefore(thirtyDaysAgo);
    }
}
