package com.givemecon.event.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Component
public class VoucherUpdateScheduler {

    private static final String EVERY_MIDNIGHT = "0 0 0 * * *";

    private final SchedulerService schedulerService;

    @Scheduled(cron = EVERY_MIDNIGHT)
    public void updateExpiredVouchers() {
        LocalDate today = LocalDate.now();
        log.info("[Log] Voucher scheduler started. Date: {}", today);
        schedulerService.updateExpired(today);
    }

    @Scheduled(cron = EVERY_MIDNIGHT)
    public void deleteNotificationsThirtyDaysOld() {
        LocalDate today = LocalDate.now();
        log.info("[Log] Notification scheduler started. Date: {}", today);
        schedulerService.deleteNotificationsThirtyDaysOld(today);
    }
}
