package com.givemecon.domain.likedvoucher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikedVoucherRepository extends JpaRepository<LikedVoucher, Long> {

    @Modifying
    @Query("delete from LikedVoucher lv " +
            "where lv.voucher.id = :voucherId and lv.member.username = :username")
    void deleteByUsernameAndVoucherId(Long voucherId, String username);
}
