package com.givemecon.domain.purchasedvoucher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PurchasedVoucherRepository extends JpaRepository<PurchasedVoucher, Long> {

    @Query("select pv, o from PurchasedVoucher pv " +
            "inner join pv.owner o on o.username = :username " +
            "where pv.id = :id")
    Optional<PurchasedVoucher> findByIdAndUsername(Long id, String username);
}
