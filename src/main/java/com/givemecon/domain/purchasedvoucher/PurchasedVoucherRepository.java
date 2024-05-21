package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PurchasedVoucherRepository extends JpaRepository<PurchasedVoucher, Long> {

    @Query("select pv from PurchasedVoucher pv " +
            "where pv.id = :id and pv.owner.username = :username")
    Optional<PurchasedVoucher> findByIdAndUsername(Long id, String username);

    List<PurchasedVoucher> findAllByOwner(Member owner);

    Page<PurchasedVoucher> findPageByOwner(Member owner, Pageable pageable);
}
