package com.givemecon.domain.voucherforsale;

import com.givemecon.config.JpaConfig;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.order.Order;
import com.givemecon.domain.order.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.givemecon.config.enums.Authority.USER;
import static com.givemecon.domain.order.OrderStatus.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Import(JpaConfig.class)
@Transactional
@DataJpaTest
class VoucherForSaleRepositoryTest {

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

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
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

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
    @DisplayName("유효기간이 만료된 모든 VoucherForSale들의 state를 EXPIRED로 변경한다.")
    void updateAllExpired() {
        // given
        LocalDate today = LocalDate.now();

        VoucherForSale expired1 = VoucherForSale.builder()
                .price(15_000L)
                .expDate(today.minusDays(1))
                .barcode("1111 1111 1111")
                .build();

        VoucherForSale expired2 = VoucherForSale.builder()
                .price(15_000L)
                .expDate(today.minusDays(1))
                .barcode("2222 2222 2222")
                .build();

        List<VoucherForSale> expiredList = List.of(expired1, expired2);
        voucherForSaleRepository.saveAll(expiredList);

        // when
        int numModified = voucherForSaleRepository.updateAllByExpDateBefore(today);

        // then
        long numExpired = voucherForSaleRepository.findAll().stream()
                .filter(voucherForSale -> voucherForSale.getStatus().equals(EXPIRED))
                .count();

        assertThat(numModified).isEqualTo(numExpired);

        voucherForSaleRepository.findAll()
                .forEach(expired -> assertThat(expired.getStatus()).isEqualTo(EXPIRED));
    }

    @Test
    @DisplayName("유효기간이 아직 남아있는 VoucherForSale들은 state가 변경되면 안된다.")
    void updateAllOnlyExpired() {
        // given
        LocalDate today = LocalDate.now();

        VoucherForSale expired = VoucherForSale.builder()
                .price(15_000L)
                .expDate(today.minusDays(1))
                .barcode("1111 1111 1111")
                .build();

        VoucherForSale notExpired = VoucherForSale.builder()
                .price(15_000L)
                .expDate(today)
                .barcode("2222 2222 2222")
                .build();

        voucherForSaleRepository.saveAll(List.of(expired, notExpired));

        // when
        int numModified = voucherForSaleRepository.updateAllByExpDateBefore(today);

        // then
        long numExpired = voucherForSaleRepository.findAll().stream()
                .filter(voucherForSale -> voucherForSale.getStatus().equals(EXPIRED))
                .count();

        assertThat(numModified).isEqualTo(numExpired);
    }

    @Test
    @DisplayName("유효기간이 지난 VoucherForSale의 state가 이미 EXPIRED라면, 해당 VoucherForSale은 변경 대상에서 제외된다.")
    void ignoreStateAlreadyExpired() {
        // given
        LocalDate today = LocalDate.now();

        VoucherForSale stateAlreadyExpired = VoucherForSale.builder()
                .price(15_000L)
                .expDate(today.minusDays(1))
                .barcode("1111 1111 1111")
                .build();

        stateAlreadyExpired.updateStatus(EXPIRED);
        voucherForSaleRepository.save(stateAlreadyExpired);

        // when
        int numModified = voucherForSaleRepository.updateAllByExpDateBefore(today);

        // then
        assertThat(numModified).isEqualTo(0);
    }

    @Test
    @DisplayName("주문별 VoucherForSale 조회")
    void findAllByOrder(@Autowired OrderRepository orderRepository) {
        // given
        Order order = orderRepository.save(new Order("ORDER-NUMBER", null));

        VoucherForSale voucherForSale = VoucherForSale.builder()
                .price(4_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build();

        voucherForSale.updateOrder(order);
        voucherForSaleRepository.save(voucherForSale);

        // when
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAllByOrder(order);

        // then
        assertThat(voucherForSaleList).isNotEmpty();

        VoucherForSale found = voucherForSaleList.get(0);
        assertThat(found).isEqualTo(voucherForSale);
        assertThat(found.getOrder()).isEqualTo(order);
    }

    @Test
    @DisplayName("주문이 취소된 VoucherForSale 상태 변경")
    void updateAllOrderCancelled(@Autowired OrderRepository orderRepository) {
        // given
        Order order = orderRepository.save(new Order("ORDER-NUMBER", null));
        order.updateStatus(CANCELLED);

        VoucherForSale voucherForSale = VoucherForSale.builder()
                .price(4_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build();

        voucherForSale.updateOrder(order);
        voucherForSale.updateStatus(ORDER_PLACED);
        voucherForSaleRepository.save(voucherForSale);

        // when
        voucherForSaleRepository.updateAllOrderCancelled();

        // then
        Optional<VoucherForSale> found = voucherForSaleRepository.findById(voucherForSale.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getOrder()).isNull();
        assertThat(found.get().getStatus()).isSameAs(FOR_SALE);
    }
}