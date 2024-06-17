package com.givemecon.util.scheduler;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Transactional
public class SchedulerService {

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    public void updateExpired(LocalDate today) {
        voucherForSaleRepository.updateAllByExpDateBefore(today);
        purchasedVoucherRepository.updateAllStatusForExpired();
    }
}
