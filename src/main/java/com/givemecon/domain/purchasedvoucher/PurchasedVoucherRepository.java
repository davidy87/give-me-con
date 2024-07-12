package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.voucher.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PurchasedVoucherRepository extends JpaRepository<PurchasedVoucher, Long> {

    @Query("select pv from PurchasedVoucher pv " +
            "where pv.id = :id and pv.owner.username = :username")
    Optional<PurchasedVoucher> findByIdAndUsername(Long id, String username);

    @Query("select pv from PurchasedVoucher pv " +
            "join fetch pv.voucher v " +
            "join fetch v.voucherKind vk " +
            "join fetch vk.voucherKindImage " +
            "where pv.id = :id and pv.owner.username = :username")
    Optional<PurchasedVoucher> findOneFetchedByIdAndUsername(Long id, String username);

    @Query("select pv from PurchasedVoucher pv " +
            "join fetch pv.voucher v " +
            "join fetch v.voucherKind vk " +
            "join fetch vk.voucherKindImage " +
            "where pv.owner.username = :username")
    List<PurchasedVoucher> findAllFetchedByUsername(String username);

    Page<PurchasedVoucher> findPageByOwner(Member owner, Pageable pageable);

    Optional<PurchasedVoucher> findByVoucher(Voucher voucher);

    @Modifying(clearAutomatically = true)
    @Query("update PurchasedVoucher pv set pv.status = 'EXPIRED' " +
            "where pv.voucher.status = 'EXPIRED' and pv.status = 'USABLE'")
    int updateAllStatusForExpired();
}
