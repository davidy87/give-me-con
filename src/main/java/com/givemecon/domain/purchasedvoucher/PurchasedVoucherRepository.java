package com.givemecon.domain.purchasedvoucher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PurchasedVoucherRepository extends JpaRepository<PurchasedVoucher, Long> {

    @Query("select pv from PurchasedVoucher pv " +
            "where pv.id = :id and pv.owner.username = :username")
    Optional<PurchasedVoucher> findByIdAndUsername(Long id, String username);
}
