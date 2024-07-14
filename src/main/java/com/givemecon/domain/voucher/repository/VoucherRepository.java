package com.givemecon.domain.voucher.repository;

import com.givemecon.domain.member.entity.Member;
import com.givemecon.domain.order.entity.Order;
import com.givemecon.domain.voucher.dto.VoucherStatus;
import com.givemecon.domain.voucher.entity.Voucher;
import com.givemecon.domain.voucherkind.entity.VoucherKind;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    List<Voucher> findAllBySeller(Member seller);

    List<Voucher> findAllByStatus(VoucherStatus status);

    List<Voucher> findAllByOrder(Order order);

    @Query("select distinct v from Voucher v " +
            "join fetch v.voucherKind vk " +
            "where vk.id = :voucherKindId and v.status = :status")
    List<Voucher> findAllByVoucherKindIdAndStatus(Long voucherKindId, VoucherStatus status);

    @Query("select v from Voucher v " +
            "where v.voucherKind = :voucherKind and v.status = :status " +
            "order by v.price")
    List<Voucher> findOneWithMinPrice(VoucherKind voucherKind, VoucherStatus status, Pageable pageable);

    @Query("select v from Voucher v " +
            "join fetch v.voucherImage " +
            "where v.id = :voucherId")
    Optional<Voucher> findOneWithImage(Long voucherId);

    @Modifying(clearAutomatically = true)
    @Query("update Voucher v set v.status = 'EXPIRED' " +
            "where v.status <> 'EXPIRED' and v.expDate < :expDate")
    int updateAllByExpDateBefore(LocalDate expDate);

    @Modifying(clearAutomatically = true)
    @Query("update Voucher v set v.order = null, v.status = 'FOR_SALE' " +
            "where v.order.status = 'CANCELLED'")
    void updateAllOrderCancelled();
}
