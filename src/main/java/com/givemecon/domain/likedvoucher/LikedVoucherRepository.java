package com.givemecon.domain.likedvoucher;

import com.givemecon.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikedVoucherRepository extends JpaRepository<LikedVoucher, Long> {

    @Modifying
    @Query("delete from LikedVoucher lv " +
            "where lv.voucher.id = :voucherId and lv.member.username = :username")
    void deleteByUsernameAndVoucherId(String username, Long voucherId);

    Page<LikedVoucher> findPageByMember(Member member, Pageable pageable);
}
