package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class PurchasedVoucherRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    Member member;

    VoucherForSale voucherForSale;

    @BeforeEach
    void setup() {
        member = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .authority(USER)
                .build());

        voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .build());
    }

    @Test
    void saveAndFindAll() {
        // given
        PurchasedVoucher purchasedVoucher = new PurchasedVoucher(voucherForSale, member);

        // when
        purchasedVoucherRepository.save(purchasedVoucher);
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getStatus()).isEqualTo(USABLE);
        assertThat(found.getOwner()).isEqualTo(member);
        assertThat(found.getVoucherForSale()).isEqualTo(voucherForSale);
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        PurchasedVoucher purchasedVoucher = new PurchasedVoucher(voucherForSale, member);

        // when
        purchasedVoucherRepository.save(purchasedVoucher);
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }
}