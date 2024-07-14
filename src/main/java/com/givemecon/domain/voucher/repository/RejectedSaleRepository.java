package com.givemecon.domain.voucher.repository;

import com.givemecon.domain.voucher.entity.RejectedSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectedSaleRepository extends JpaRepository<RejectedSale, Long> {
}
