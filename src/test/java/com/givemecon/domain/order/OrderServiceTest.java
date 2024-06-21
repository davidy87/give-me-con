package com.givemecon.domain.order;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.order.exception.InvalidOrderException;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.givemecon.domain.order.OrderDto.*;
import static com.givemecon.domain.order.OrderStatus.CONFIRMED;
import static com.givemecon.domain.order.OrderStatus.IN_PROGRESS;
import static com.givemecon.domain.order.exception.OrderErrorCode.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    VoucherForSaleRepository voucherForSaleRepository;

    @Mock
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Mock
    Member seller;

    @Mock
    VoucherForSale voucherForSale;

    @Mock
    Order order;

    OrderService orderService;

    @BeforeEach
    void setup() {
        orderService =
                new OrderService(orderRepository, memberRepository, voucherForSaleRepository, purchasedVoucherRepository);
    }

    @Nested
    @DisplayName("주문 요청 테스트")
    class OrderPlacingTest {

        @BeforeEach
        void setup() {
            Mockito.when(memberRepository.findById(any(Long.class)))
                    .thenReturn(Optional.of(seller));

            Mockito.when(voucherForSaleRepository.findById(any(Long.class)))
                    .thenReturn(Optional.of(voucherForSale));

            Mockito.when(voucherForSale.getSeller())
                    .thenReturn(seller);

            Mockito.when(orderRepository.save(any(Order.class)))
                    .thenReturn(order);
        }

        @Test
        @DisplayName("정상적인 주문 요청 테스트")
        void makeValidOrder() {
            // given
            Mockito.when(order.getId()).thenReturn(1L);
            Mockito.when(voucherForSale.getStatus()).thenReturn(FOR_SALE);

            Long buyerId = 1L;
            List<Long> voucherForSaleIdList = List.of(1L, 2L, 3L);
            OrderRequest orderRequest = new OrderRequest(buyerId, voucherForSaleIdList);

            // when
            OrderNumberResponse response = orderService.placeOrder(orderRequest);

            // then
            assertThat(response.getOrderNumber()).isEqualTo(order.getId());
        }

        @Test
        @DisplayName("주문 요청 예외 1 - 구매할 VoucherForSale의 status가 FOR_SALE이 아닐 경우 주문 요청 실패")
        void notForSaleOrder() {
            // given
            Mockito.when(voucherForSale.getStatus()).thenReturn(NOT_YET_PERMITTED);

            Long buyerId = 1L;
            List<Long> voucherForSaleIdList = List.of(1L, 2L, 3L);
            OrderRequest orderRequest = new OrderRequest(buyerId, voucherForSaleIdList);

            // when & then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequest))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ITEM_NOT_FOR_SALE.getMessage());
        }

        @Test
        @DisplayName("주문 요청 예외 2 - 구매할 VoucherForSale의 seller가 존재하지 않을 경우 주문 요청 실패")
        void unavailableSellerOrder() {
            // given
            Mockito.when(voucherForSale.getStatus()).thenReturn(FOR_SALE);
            Mockito.when(voucherForSale.getSeller().isDeleted()).thenReturn(true);

            Long buyerId = 1L;
            List<Long> voucherForSaleIdList = List.of(1L, 2L, 3L);
            OrderRequest orderRequest = new OrderRequest(buyerId, voucherForSaleIdList);

            // when & then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequest))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(SELLER_UNAVAILABLE.getMessage());
        }
    }

    @Test
    @DisplayName("정상적인 주문 조회 처리")
    void findOrder() {
        // given
        Long orderNumber = 1L;
        List<VoucherForSale> voucherForSaleList = List.of(voucherForSale);

        Mockito.when(orderRepository.findById(orderNumber))
                .thenReturn(Optional.of(order));

        Mockito.when(voucherForSaleRepository.findAllByOrder(order))
                .thenReturn(voucherForSaleList);

        Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
        Mockito.when(voucherForSale.getStatus()).thenReturn(FOR_SALE);

        // when
        OrderSummary orderSummary = orderService.findOrder(orderNumber);

        // then
        assertThat(orderSummary.getStatus()).isSameAs(IN_PROGRESS);
        assertThat(orderSummary.getQuantity()).isEqualTo(voucherForSaleList.size());
        assertThat(orderSummary.getTotalPrice()).isEqualTo(voucherForSale.getPrice() * voucherForSaleList.size());
        assertThat(orderSummary.getOrderItems().size()).isEqualTo(orderSummary.getQuantity());

        orderSummary.getOrderItems()
                .forEach(orderItem -> {
                    assertThat(orderItem.getStatus()).isSameAs(FOR_SALE);
                    assertThat(orderItem.getVoucherForSaleId()).isEqualTo(voucherForSale.getId());
                });
    }

    @Test
    @DisplayName("주문 조회 예외 - VoucherForSale의 status가 FOR_SALE이 아닌 경우, 해당 품목은 조회에서 제외된다.")
    void ignoreItemsNotForSale() {
        // given
        Long orderNumber = 1L;
        List<VoucherForSale> voucherForSaleList = List.of(voucherForSale);

        Mockito.when(orderRepository.findById(orderNumber))
                .thenReturn(Optional.of(order));

        Mockito.when(voucherForSaleRepository.findAllByOrder(order))
                .thenReturn(voucherForSaleList);

        Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
        Mockito.when(voucherForSale.getStatus()).thenReturn(NOT_YET_PERMITTED);

        // when
        OrderSummary orderSummary = orderService.findOrder(orderNumber);

        // then
        assertThat(orderSummary.getStatus()).isSameAs(IN_PROGRESS);
        assertThat(orderSummary.getQuantity()).isEqualTo(0);
        assertThat(orderSummary.getTotalPrice()).isEqualTo(0);
        assertThat(orderSummary.getOrderItems().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("정상적인 주문 체결 요청")
    void confirmOrder() {
        // given
        Member buyer = Member.builder()
                .username("tester")
                .build();

        Mockito.when(order.getBuyer()).thenReturn(buyer);

        Mockito.when(voucherForSale.getStatus()).thenReturn(FOR_SALE);

        Mockito.when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        Mockito.when(voucherForSaleRepository.findAllByOrder(order))
                .thenReturn(List.of(voucherForSale));

        // when
        OrderNumberResponse response = orderService.confirmOrder(order.getId(), buyer.getUsername());

        // then
        assertThat(response.getOrderNumber()).isEqualTo(order.getId());
    }

    @Test
    @DisplayName("주문 체결 요청 예외 1 - 이미 체결된 주문은 처리하지 않는다.")
    void orderAlreadyConfirmed() {
        // given
        String username = "tester";
        Mockito.when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        Mockito.when(order.getStatus()).thenReturn(CONFIRMED);

        // when & then
        assertThatThrownBy(() -> orderService.confirmOrder(order.getId(), username))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage(ORDER_ALREADY_CONFIRMED.getMessage());
    }

    @Test
    @DisplayName("주문 체결 요청 예외 2 - 주문 정보에 있는 구매자의 username이 사용자의 username과 다르다면 해당 요청을 처리하지 않는다.")
    void buyerNotMatch() {
        // given
        Member buyer = Member.builder()
                .username("tester")
                .build();

        Mockito.when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        Mockito.when(order.getBuyer()).thenReturn(buyer);

        // when & then
        assertThatThrownBy(() -> orderService.confirmOrder(order.getId(), "notTester"))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage(BUYER_NOT_MATCH.getMessage());
    }
}