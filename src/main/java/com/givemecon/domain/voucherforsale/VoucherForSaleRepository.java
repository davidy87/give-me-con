package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface VoucherForSaleRepository extends JpaRepository<VoucherForSale, Long> {

    List<VoucherForSale> findAllBySeller(Member seller);

    List<VoucherForSale> findAllByStatus(VoucherForSaleStatus status);

    List<VoucherForSale> findAllByOrder(Order order);

    @Modifying(clearAutomatically = true)
    @Query("update VoucherForSale vfs set vfs.status = 'EXPIRED' " +
            "where vfs.status <> 'EXPIRED' and vfs.expDate < :expDate")
    int updateAllByExpDateBefore(LocalDate expDate);
}
