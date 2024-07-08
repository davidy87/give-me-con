package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.order.Order;
import com.givemecon.domain.voucher.Voucher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface VoucherForSaleRepository extends JpaRepository<VoucherForSale, Long> {

    List<VoucherForSale> findAllBySeller(Member seller);

    List<VoucherForSale> findAllByStatus(VoucherForSaleStatus status);

    List<VoucherForSale> findAllByOrder(Order order);

    @Query("select distinct vfs from VoucherForSale vfs " +
            "join fetch vfs.voucher v " +
            "where v.id = :voucherId and vfs.status = :status")
    List<VoucherForSale> findAllByVoucherIdAndStatus(Long voucherId, VoucherForSaleStatus status);

    @Query("select vfs from VoucherForSale vfs " +
            "where vfs.voucher = :voucher and vfs.status = :status " +
            "order by vfs.price")
    List<VoucherForSale> findOneWithMinPrice(Voucher voucher, VoucherForSaleStatus status, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update VoucherForSale vfs set vfs.status = 'EXPIRED' " +
            "where vfs.status <> 'EXPIRED' and vfs.expDate < :expDate")
    int updateAllByExpDateBefore(LocalDate expDate);

    @Modifying(clearAutomatically = true)
    @Query("update VoucherForSale vfs set vfs.order = null, vfs.status = 'FOR_SALE' " +
            "where vfs.order.status = 'CANCELLED'")
    void updateAllOrderCancelled();
}
