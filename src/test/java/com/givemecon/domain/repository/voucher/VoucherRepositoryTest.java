package com.givemecon.domain.repository.voucher;

import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.order.Order;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.repository.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.givemecon.domain.entity.member.Role.USER;
import static com.givemecon.domain.entity.order.OrderStatus.CANCELLED;
import static com.givemecon.domain.entity.voucher.VoucherStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class VoucherRepositoryTest extends RepositoryTest {

    @Test
    void saveAndFindAll() {
        // given
        Voucher voucher = Voucher.builder()
                .price(15_000L)
                .expDate(LocalDate.now().plusDays(1))
                .barcode("1111 1111 1111")
                .build();

        // when
        voucherRepository.save(voucher);
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        Voucher found = voucherList.get(0);
        assertThat(found.getPrice()).isEqualTo(voucher.getPrice());
        assertThat(found.getExpDate()).isEqualTo(voucher.getExpDate());
        assertThat(found.getBarcode()).isEqualTo(voucher.getBarcode());
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherRepository.save(Voucher.builder()
                .price(10_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build());

        // when
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        Voucher found = voucherList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Test
    void findAllBySeller() {
        // given
        Member seller = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(USER)
                .build());

        Voucher voucher = Voucher.builder()
                .price(15_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .seller(seller)
                .build();

        voucherRepository.save(voucher);

        // when
        List<Voucher> voucherList = voucherRepository.findAllBySeller(seller);

        // then
        assertThat(voucherList).isNotEmpty();
        voucherList.forEach(found -> {
            assertThat(found).isEqualTo(voucher);
            assertThat(found.getSeller()).isEqualTo(seller);
        });
    }

    @Test
    void findAllByStatus() {
        // given
        Voucher voucher = Voucher.builder()
                .price(15_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build();

        voucherRepository.save(voucher);

        // when
        List<Voucher> voucherList = voucherRepository.findAllByStatus(SALE_REQUESTED);

        // then
        Voucher found = voucherList.get(0);
        assertThat(voucherList).isNotEmpty();
        assertThat(found).isEqualTo(voucher);
        assertThat(found.getStatus()).isSameAs(SALE_REQUESTED);
    }

    @Test
    @DisplayName("주문별 Voucher 조회")
    void findAllByOrder() {
        // given
        Order order = orderRepository.save(new Order("ORDER-NUMBER", null));

        Voucher voucher = Voucher.builder()
                .price(4_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build();

        voucher.updateOrder(order);
        voucherRepository.save(voucher);

        // when
        List<Voucher> voucherList = voucherRepository.findAllByOrder(order);

        // then
        assertThat(voucherList).isNotEmpty();

        Voucher found = voucherList.get(0);
        assertThat(found).isEqualTo(voucher);
        assertThat(found.getOrder()).isEqualTo(order);
    }

    @Nested
    @DisplayName("JPQL 테스트")
    class JPQLTest {

        @Test
        void findAllByStatusAndUsername() {
            // given
            Member seller = memberRepository.save(Member.builder()
                    .email("tester@gmail.com")
                    .username("tester")
                    .role(USER)
                    .build());

            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .price(15_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .seller(seller)
                    .build());

            // when
            List<Voucher> result = voucherRepository.findAllByStatusAndUsername(SALE_REQUESTED, seller.getUsername());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(voucher.getId());
        }

        @Test
        @DisplayName("사용자가 판매 중인 기프티콘들을 제외한 모든 기프티콘들을 기프티콘 종류 id와 기프티콘의 상태별로 조회")
        void findAllExceptSellersByVoucherKindIdAndStatus() {
            // given
            Member seller = memberRepository.save(Member.builder()
                    .email("seller@gmail.com")
                    .username("seller")
                    .role(USER)
                    .build());

            Member buyer = memberRepository.save(Member.builder()
                    .email("buyer@gmail.com")
                    .username("buyer")
                    .role(USER)
                    .build());

            VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                    .title("voucherKind")
                    .build());

            Voucher voucher = Voucher.builder()
                    .price(4_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .voucherKind(voucherKind)
                    .seller(seller)
                    .build();

            voucher.updateStatus(FOR_SALE);
            voucherRepository.save(voucher);

            // when
            List<Voucher> result =
                    voucherRepository.findAllExceptSellersByVoucherKindIdAndStatus(voucherKind.getId(), FOR_SALE, buyer.getUsername());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSeller().getUsername()).isNotEqualTo(buyer.getUsername());
        }

        @Test
        @DisplayName("기프티콘 종류별 최소 가격 테스트 1")
        void findOneWithMinPrice() {
            // given
            VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                    .title("voucherKind")
                    .build());

            List<Voucher> voucherList = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {
                Voucher voucher = Voucher.builder()
                        .price(4_000L * i)
                        .expDate(LocalDate.now())
                        .barcode("1111 1111 1111")
                        .voucherKind(voucherKind)
                        .build();

                voucher.updateStatus(FOR_SALE);
                voucherList.add(voucher);
            }

            voucherRepository.saveAll(voucherList);

            // when
            List<Voucher> found =
                    voucherRepository.findOneWithMinPrice(voucherKind, FOR_SALE, PageRequest.of(0, 1));

            // then
            assertThat(found.size()).isEqualTo(1);
            assertThat(found.get(0).getPrice()).isEqualTo(4_000L);
        }

        @Test
        @DisplayName("기프티콘 종류별 최소 가격 테스트 2 - 사용자가 판매하고 있는 기프티콘이 있다면, 해당 기프티콘은 최소 가격 계산에 포함되지 않는다.")
        void findOneWithMinPriceExcludingUserSelling() {
            // given
            Member seller = memberRepository.save(Member.builder()
                    .email("seller@gmail.com")
                    .username("seller")
                    .role(USER)
                    .build());

            VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                    .title("voucherKind")
                    .build());

            List<Voucher> voucherList = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {
                Voucher voucher = Voucher.builder()
                        .price(4_000L * i)
                        .expDate(LocalDate.now())
                        .barcode("1111 1111 1111")
                        .voucherKind(voucherKind)
                        .seller(seller)
                        .build();

                voucher.updateStatus(FOR_SALE);
                voucherList.add(voucher);
            }

            voucherRepository.saveAll(voucherList);

            // when
            List<Voucher> found =
                    voucherRepository.findOneWithMinPrice(seller, voucherKind, FOR_SALE, PageRequest.of(0, 1));

            // then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Voucher & VoucherImage fetch join 테스트")
        void findOneWithImage() {
            // given
            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey")
                    .imageUrl("imageUrl")
                    .originalName("originalName")
                    .build());

            Voucher voucher = Voucher.builder()
                    .price(4_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .voucherImage(voucherImage)
                    .build();

            voucher.updateStatus(FOR_SALE);
            voucherRepository.save(voucher);

            // when
            Optional<Voucher> result = voucherRepository.findOneWithImage(voucher.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(voucher.getId());
            assertThat(result.get().getImageUrl()).isEqualTo(voucherImage.getImageUrl());
        }
    }

    @Nested
    @DisplayName("벌크 수정 테스트")
    class BulkUpdateTest {

        @Test
        @DisplayName("유효기간이 만료된 모든 Voucher의 state 변경 테스트 1 - 일반 흐름")
        void updateAllExpired() {
            // given
            LocalDate today = LocalDate.now();

            Voucher expired1 = Voucher.builder()
                    .price(15_000L)
                    .expDate(today.minusDays(1))
                    .barcode("1111 1111 1111")
                    .build();

            Voucher expired2 = Voucher.builder()
                    .price(15_000L)
                    .expDate(today.minusDays(1))
                    .barcode("2222 2222 2222")
                    .build();

            List<Voucher> expiredList = List.of(expired1, expired2);
            voucherRepository.saveAll(expiredList);

            // when
            int numModified = voucherRepository.updateAllByExpDateBefore(today);

            // then
            long numExpired = voucherRepository.findAll().stream()
                    .filter(voucherForSale -> voucherForSale.getStatus().equals(EXPIRED))
                    .count();

            assertThat(numModified).isEqualTo(numExpired);

            voucherRepository.findAll()
                    .forEach(expired -> assertThat(expired.getStatus()).isEqualTo(EXPIRED));
        }

        @Test
        @DisplayName(
                "유효기간이 만료된 모든 Voucher의 state 변경 테스트 2 " +
                        "- 유효기간이 아직 남아있는 Voucher들은 state가 변경되면 안된다."
        )
        void ignoreNotExpiredWhenUpdateAllExpired() {
            // given
            LocalDate today = LocalDate.now();

            Voucher expired = Voucher.builder()
                    .price(15_000L)
                    .expDate(today.minusDays(1))
                    .barcode("1111 1111 1111")
                    .build();

            Voucher notExpired = Voucher.builder()
                    .price(15_000L)
                    .expDate(today)
                    .barcode("2222 2222 2222")
                    .build();

            voucherRepository.saveAll(List.of(expired, notExpired));

            // when
            int numModified = voucherRepository.updateAllByExpDateBefore(today);

            // then
            long numExpired = voucherRepository.findAll().stream()
                    .filter(voucherForSale -> voucherForSale.getStatus().equals(EXPIRED))
                    .count();

            assertThat(numModified).isEqualTo(numExpired);
        }

        @Test
        @DisplayName(
                "유효기간이 만료된 모든 Voucher의 state 변경 테스트 3 " +
                        "- 유효기간이 지난 Voucher의 state가 이미 EXPIRED라면, 해당 Voucher은 변경 대상에서 제외된다."
        )
        void ignoreAlreadyExpiredWhenUpdateAllExpired() {
            // given
            LocalDate today = LocalDate.now();

            Voucher stateAlreadyExpired = Voucher.builder()
                    .price(15_000L)
                    .expDate(today.minusDays(1))
                    .barcode("1111 1111 1111")
                    .build();

            stateAlreadyExpired.updateStatus(EXPIRED);
            voucherRepository.save(stateAlreadyExpired);

            // when
            int numModified = voucherRepository.updateAllByExpDateBefore(today);

            // then
            assertThat(numModified).isEqualTo(0);
        }

        @Test
        @DisplayName("주문이 취소된 모든 Voucher 상태 변경")
        void updateAllOrderCancelled() {
            // given
            Order order = orderRepository.save(new Order("ORDER-NUMBER", null));
            order.updateStatus(CANCELLED);

            Voucher voucher = Voucher.builder()
                    .price(4_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .build();

            voucher.updateOrder(order);
            voucher.updateStatus(ORDER_PLACED);
            voucherRepository.save(voucher);

            // when
            voucherRepository.updateAllOrderCancelled();

            // then
            Optional<Voucher> found = voucherRepository.findById(voucher.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getOrder()).isNull();
            assertThat(found.get().getStatus()).isSameAs(FOR_SALE);
        }
    }
}