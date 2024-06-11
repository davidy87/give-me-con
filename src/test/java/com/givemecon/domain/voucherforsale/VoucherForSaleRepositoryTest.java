package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.givemecon.config.enums.Authority.USER;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class VoucherForSaleRepositoryTest {

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Test
    void saveAndFindAll() {
        // given
        VoucherForSale voucherForSale = VoucherForSale.builder()
                .price(15_000L)
                .expDate(LocalDate.now().plusDays(1))
                .barcode("1111 1111 1111")
                .build();

        // when
        voucherForSaleRepository.save(voucherForSale);
        List<VoucherForSale> voucherList = voucherForSaleRepository.findAll();

        // then
        VoucherForSale found = voucherList.get(0);
        assertThat(found.getPrice()).isEqualTo(voucherForSale.getPrice());
        assertThat(found.getExpDate()).isEqualTo(voucherForSale.getExpDate());
        assertThat(found.getBarcode()).isEqualTo(voucherForSale.getBarcode());
    }

    @Test
    void findAllBySeller(@Autowired MemberRepository memberRepository) {
        // given
        Member seller = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .authority(USER)
                .build());

        VoucherForSale voucherForSale = VoucherForSale.builder()
                .price(15_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build();

        voucherForSale.updateSeller(seller);
        voucherForSaleRepository.save(voucherForSale);

        // when
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAllBySeller(seller);

        // then
        assertThat(voucherForSaleList).isNotEmpty();
        voucherForSaleList.forEach(found -> {
            assertThat(found).isEqualTo(voucherForSale);
            assertThat(found.getSeller()).isEqualTo(seller);
        });
    }

    @Test
    void findAllByStatus() {
        // given
        VoucherForSale voucherForSale = VoucherForSale.builder()
                .price(15_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build();

        voucherForSaleRepository.save(voucherForSale);

        // when
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAllByStatus(NOT_YET_PERMITTED);

        // then
        VoucherForSale found = voucherForSaleList.get(0);
        assertThat(voucherForSaleList).isNotEmpty();
        assertThat(found).isEqualTo(voucherForSale);
        assertThat(found.getStatus()).isSameAs(NOT_YET_PERMITTED);
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherForSaleRepository.save(VoucherForSale.builder()
                .price(10_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build());

        // when
        List<VoucherForSale> voucherList = voucherForSaleRepository.findAll();

        // then
        VoucherForSale found = voucherList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}