package com.givemecon.domain.order;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.order.exception.InvalidOrderException;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import com.givemecon.domain.voucher.Voucher;
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
import java.util.UUID;

import static com.givemecon.domain.order.OrderDto.*;
import static com.givemecon.domain.order.OrderStatus.*;
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
    Member buyer;

    @Mock
    VoucherForSale voucherForSale;

    @Mock
    Order order;

    OrderService orderService;

    @BeforeEach
    void setup() {
        Mockito.when(buyer.getUsername()).thenReturn("buyer");

        orderService =
                new OrderService(orderRepository, memberRepository, voucherForSaleRepository, purchasedVoucherRepository);
    }

    @Nested
    @DisplayName("주문 요청 테스트")
    class OrderPlacingTest {

        @BeforeEach
        void setup() {
            Mockito.when(memberRepository.findByUsername(any(String.class)))
                    .thenReturn(Optional.of(buyer));

            Mockito.when(voucherForSaleRepository.findById(any(Long.class)))
                    .thenReturn(Optional.of(voucherForSale));

            Mockito.when(voucherForSale.getSeller())
                    .thenReturn(seller);

            Mockito.when(orderRepository.save(any(Order.class)))
                    .thenReturn(order);
        }

        @Test
        @DisplayName("정상적인 주문 요청 테스트")
        void placeValidOrder() {
            // given
            Mockito.when(voucherForSale.getStatus()).thenReturn(FOR_SALE);
            Mockito.when(buyer.getId()).thenReturn(1L);
            Mockito.when(seller.getId()).thenReturn(2L);
            Mockito.when(order.getOrderNumber()).thenReturn(UUID.randomUUID().toString());

            List<Long> voucherForSaleIdList = List.of(1L, 2L, 3L);
            OrderRequest orderRequest = new OrderRequest(voucherForSaleIdList);

            // when
            OrderNumberResponse response = orderService.placeOrder(orderRequest, buyer.getUsername());

            // then
            assertThat(response.getOrderNumber()).isEqualTo(order.getOrderNumber());
        }

        @Test
        @DisplayName("주문 요청 예외 1 - 구매할 VoucherForSale의 status가 FOR_SALE이 아닐 경우 주문 요청 실패")
        void notForSaleOrder() {
            // given
            Mockito.when(voucherForSale.getStatus()).thenReturn(NOT_YET_PERMITTED);
            OrderRequest orderRequest = new OrderRequest(List.of(1L, 2L, 3L));

            // when & then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequest, buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ITEM_NOT_FOR_SALE.getMessage());
        }

        @Test
        @DisplayName("주문 요청 예외 2 - 구매할 VoucherForSale의 seller가 존재하지 않을 경우 주문 요청 실패")
        void unavailableSellerOrder() {
            // given
            Mockito.when(voucherForSale.getStatus()).thenReturn(FOR_SALE);
            Mockito.when(voucherForSale.getSeller().isDeleted()).thenReturn(true);
            OrderRequest orderRequest = new OrderRequest(List.of(1L, 2L, 3L));

            // when & then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequest, buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(SELLER_UNAVAILABLE.getMessage());
        }

        @Test
        @DisplayName("주문 요청 예외 3 - 구매자와 구매할 기프티콘의 판매자가 같을 경우 예외 처리")
        void buyerEqualsSeller() {
            // given
            Mockito.when(voucherForSale.getStatus()).thenReturn(FOR_SALE);
            Mockito.when(voucherForSale.getSeller()).thenReturn(buyer);
            OrderRequest orderRequest = new OrderRequest(List.of(1L, 2L, 3L));

            // when & then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequest, buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(BUYER_EQUALS_SELLER.getMessage());
        }

    }

    @Nested
    @DisplayName("주문 조회 테스트")
    class OrderInquiryTest {

        @BeforeEach
        void setup() {
            Mockito.when(orderRepository.findByOrderNumber(any(String.class)))
                    .thenReturn(Optional.of(order));

            Mockito.when(order.getOrderNumber()).thenReturn(UUID.randomUUID().toString());
        }

        @Test
        @DisplayName("정상적인 주문 조회 처리")
        void findOrder(@Mock Brand brand, @Mock Voucher voucher) {
            // given
            List<VoucherForSale> voucherForSaleList = List.of(voucherForSale);

            Mockito.when(voucherForSaleRepository.findAllByOrder(order))
                    .thenReturn(voucherForSaleList);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(voucherForSale.getStatus()).thenReturn(ORDER_PLACED);
            Mockito.when(voucherForSale.getVoucher()).thenReturn(voucher);
            Mockito.when(voucher.getBrand()).thenReturn(brand);
            Mockito.when(voucher.getImageUrl()).thenReturn("imageUrl");
            Mockito.when(brand.getName()).thenReturn("Brand");

            // when
            OrderSummary orderSummary = orderService.findOrder(order.getOrderNumber(), buyer.getUsername());

            // then
            assertThat(orderSummary.getOrderNumber()).isEqualTo(order.getOrderNumber());
            assertThat(orderSummary.getStatus()).isSameAs(IN_PROGRESS);
            assertThat(orderSummary.getCustomerName()).isEqualTo(buyer.getUsername());
            assertThat(orderSummary.getQuantity()).isEqualTo(voucherForSaleList.size());
            assertThat(orderSummary.getTotalPrice()).isEqualTo(voucherForSale.getPrice() * voucherForSaleList.size());
            assertThat(orderSummary.getOrderItems().size()).isEqualTo(orderSummary.getQuantity());

            orderSummary.getOrderItems()
                    .forEach(orderItem -> {
                        assertThat(orderItem.getStatus()).isSameAs(ORDER_PLACED);
                        assertThat(orderItem.getVoucherForSaleId()).isEqualTo(voucherForSale.getId());
                    });
        }

        @Test
        @DisplayName("주문 조회 예외 1 - VoucherForSale의 status가 ORDER_PLACED가 아닌 경우, 예외를 던진다.")
        void ignoreItemsNotForSale() {
            // given
            List<VoucherForSale> voucherForSaleList = List.of(voucherForSale);

            Mockito.when(voucherForSaleRepository.findAllByOrder(order))
                    .thenReturn(voucherForSaleList);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(voucherForSale.getStatus()).thenReturn(NOT_YET_PERMITTED);

            // when & then
            assertThatThrownBy(() -> orderService.findOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ITEM_ORDER_NOT_PLACED.getMessage());
        }

        @Test
        @DisplayName("주문 조회 예외 2 - 주문 정보에 있는 구매자의 username이 사용자의 username과 다르다면 해당 요청을 처리하지 않는다.")
        void buyerNotMatchWhenFindOrder() {
            // given
            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getBuyer()).thenReturn(buyer);

            // when & then
            assertThatThrownBy(() -> orderService.findOrder(order.getOrderNumber(), "notBuyer"))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(BUYER_NOT_MATCH.getMessage());
        }
    }

    @Nested
    @DisplayName("주문 체결 테스트")
    class OrderConfirmationTest {

        @BeforeEach
        void setup() {
            Mockito.when(orderRepository.findByOrderNumber(any(String.class)))
                    .thenReturn(Optional.of(order));

            Mockito.when(order.getOrderNumber()).thenReturn(UUID.randomUUID().toString());
        }

        @Test
        @DisplayName("정상적인 주문 체결 요청")
        void confirmOrder() {
            // given
            Mockito.when(order.getBuyer()).thenReturn(buyer);

            Mockito.when(voucherForSaleRepository.findAllByOrder(order))
                    .thenReturn(List.of(voucherForSale));

            Mockito.when(voucherForSale.getStatus()).thenReturn(ORDER_PLACED);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);

            // when
            OrderNumberResponse response = orderService.confirmOrder(order.getOrderNumber(), buyer.getUsername());

            // then
            assertThat(response.getOrderNumber()).isEqualTo(order.getOrderNumber());
        }

        @Test
        @DisplayName("주문 체결 요청 예외 1 - 이미 체결된 주문은 처리하지 않는다.")
        void orderAlreadyConfirmedWhenConfirm() {
            // given
            Mockito.when(order.getStatus()).thenReturn(CONFIRMED);

            // when & then
            assertThatThrownBy(() -> orderService.confirmOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ORDER_ALREADY_CONFIRMED.getMessage());
        }

        @Test
        @DisplayName("주문 체결 요청 예외 2 - 이미 취소된 주문은 처리하지 않는다.")
        void orderAlreadyCancelledWhenConfirm() {
            // given
            Mockito.when(order.getStatus()).thenReturn(CANCELLED);

            // when & then
            assertThatThrownBy(() -> orderService.confirmOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ORDER_ALREADY_CANCELLED.getMessage());
        }

        @Test
        @DisplayName("주문 체결 요청 예외 3 - 주문 정보에 있는 구매자의 username이 사용자의 username과 다르다면 해당 요청을 처리하지 않는다.")
        void buyerNotMatchWhenConfirm() {
            // given
            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);

            // when & then
            assertThatThrownBy(() -> orderService.confirmOrder(order.getOrderNumber(), "notBuyer"))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(BUYER_NOT_MATCH.getMessage());
        }

        @Test
        @DisplayName("주문 체결 요청 예외 4 - 주문할 VoucherForSale 중 하나라도 ORDER_PLACED 상태가 아닐 경우, 체결 처리하지 않는다.")
        void itemNotForSaleWhenConfirm() {
            // given
            Mockito.when(order.getBuyer()).thenReturn(buyer);

            Mockito.when(voucherForSaleRepository.findAllByOrder(order))
                    .thenReturn(List.of(voucherForSale));

            Mockito.when(voucherForSale.getStatus()).thenReturn(SOLD);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);

            // when & then
            assertThatThrownBy(() -> orderService.confirmOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ITEM_ORDER_NOT_PLACED.getMessage());
        }
    }

    @Nested
    @DisplayName("주문 취소 테스트")
    class OrderCancellationTest {

        @BeforeEach
        void setup() {
            Mockito.when(orderRepository.findByOrderNumber(any(String.class)))
                    .thenReturn(Optional.of(order));

            Mockito.when(order.getOrderNumber()).thenReturn(UUID.randomUUID().toString());
        }

        @Test
        @DisplayName("정상적인 주문 취소 처리")
        void cancelOrder() {
            // given
            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);

            // when
            OrderNumberResponse result = orderService.cancelOrder(order.getOrderNumber(), buyer.getUsername());

            // then
            assertThat(result.getOrderNumber()).isEqualTo(order.getOrderNumber());
        }

        @Test
        @DisplayName("주문 취소 처리 예외 1 - 이미 체결된 주문일 경우, 취소되지 않는다.")
        void orderAlreadyConfirmedWhenCancel() {
            // given
            Mockito.when(order.getStatus()).thenReturn(CONFIRMED);

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ORDER_ALREADY_CONFIRMED.getMessage());
        }

        @Test
        @DisplayName("주문 최소 처리 예외 2 - 이미 취소 처리가 된 주문일 경우, 예외를 던진다.")
        void orderAlreadyCanceledWhenCancel() {
            // given
            Mockito.when(order.getStatus()).thenReturn(CANCELLED);

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ORDER_ALREADY_CANCELLED.getMessage());
        }

        @Test
        @DisplayName("주문 최소 처리 예외 3 - 취소할 주문의 주문자 정보와 취소 요청을 한 사용자의 정보와 일치하지 않을 경우, 취소되지 않는다.")
        void buyerNotMatchWhenCancel() {
            // given
            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(order.getOrderNumber(), "notBuyer"))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(BUYER_NOT_MATCH.getMessage());
        }
    }
}