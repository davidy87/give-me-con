package com.givemecon.util.scheduler;

import com.givemecon.domain.repository.PurchasedVoucherRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Transactional
public class SchedulerService {

    private final VoucherRepository voucherRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    public void updateExpired(LocalDate today) {
        voucherRepository.updateAllByExpDateBefore(today);
        purchasedVoucherRepository.updateAllStatusForExpired();
    }
}
