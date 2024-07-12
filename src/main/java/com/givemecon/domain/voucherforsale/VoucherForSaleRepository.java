package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.order.Order;
import com.givemecon.domain.voucherkind.VoucherKind;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VoucherForSaleRepository extends JpaRepository<VoucherForSale, Long> {

    List<VoucherForSale> findAllBySeller(Member seller);

    List<VoucherForSale> findAllByStatus(VoucherForSaleStatus status);

    List<VoucherForSale> findAllByOrder(Order order);

    @Query("select distinct vfs from VoucherForSale vfs " +
            "join fetch vfs.voucherKind vk " +
            "where vk.id = :voucherKindId and vfs.status = :status")
    List<VoucherForSale> findAllByVoucherKindIdAndStatus(Long voucherKindId, VoucherForSaleStatus status);

    @Query("select vfs from VoucherForSale vfs " +
            "where vfs.voucherKind = :voucherKind and vfs.status = :status " +
            "order by vfs.price")
    List<VoucherForSale> findOneWithMinPrice(VoucherKind voucherKind, VoucherForSaleStatus status, Pageable pageable);

    @Query("select vfs from VoucherForSale vfs " +
            "join fetch vfs.voucherForSaleImage " +
            "where vfs.id = :voucherForSaleId")
    Optional<VoucherForSale> findOneWithImage(Long voucherForSaleId);

    @Modifying(clearAutomatically = true)
    @Query("update VoucherForSale vfs set vfs.status = 'EXPIRED' " +
            "where vfs.status <> 'EXPIRED' and vfs.expDate < :expDate")
    int updateAllByExpDateBefore(LocalDate expDate);

    @Modifying(clearAutomatically = true)
    @Query("update VoucherForSale vfs set vfs.order = null, vfs.status = 'FOR_SALE' " +
            "where vfs.order.status = 'CANCELLED'")
    void updateAllOrderCancelled();
}
