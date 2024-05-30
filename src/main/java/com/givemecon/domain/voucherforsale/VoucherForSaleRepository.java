package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherForSaleRepository extends JpaRepository<VoucherForSale, Long> {

    List<VoucherForSale> findAllBySeller(Member seller);
}
