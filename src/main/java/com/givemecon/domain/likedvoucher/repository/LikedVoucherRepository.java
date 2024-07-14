package com.givemecon.domain.likedvoucher.repository;

import com.givemecon.domain.likedvoucher.entity.LikedVoucher;
import com.givemecon.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikedVoucherRepository extends JpaRepository<LikedVoucher, Long> {

    @Query("select lv from LikedVoucher lv " +
            "join fetch lv.voucherKind vk " +
            "join fetch vk.voucherKindImage " +
            "where lv.member.username = :username")
    List<LikedVoucher> findAllFetchedByUsername(String username);

    Page<LikedVoucher> findPageByMember(Member member, Pageable pageable);

    @Modifying
    @Query("delete from LikedVoucher lv " +
            "where lv.voucherKind.id = :voucherKindId and lv.member.username = :username")
    void deleteByUsernameAndVoucherKindId(String username, Long voucherKindId);
}