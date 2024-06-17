package com.givemecon.util.scheduler;

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
    public void updateExpiredEveryMidnight() {
        LocalDate today = LocalDate.now();
        log.info("[Log] --- VoucherForSale scheduler started ---");
        log.info("[Log] Date: {}", today);
        schedulerService.updateExpired(today);
    }
}
