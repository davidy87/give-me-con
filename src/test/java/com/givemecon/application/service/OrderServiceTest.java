package com.givemecon.application.service;

import com.givemecon.application.exception.order.InvalidOrderException;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.order.Order;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.OrderRepository;
import com.givemecon.domain.repository.PurchasedVoucherRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.givemecon.application.dto.OrderDto.*;
import static com.givemecon.application.exception.order.OrderErrorCode.*;
import static com.givemecon.domain.entity.order.OrderStatus.*;
import static com.givemecon.domain.entity.voucher.VoucherStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    VoucherRepository voucherRepository;

    @Mock
    PurchasedVoucherRepository purchasedVoucherRepository;

    @InjectMocks
    OrderService orderService;

    @Mock
    Member seller;

    @Mock
    Member buyer;

    @Mock
    Voucher voucher;

    @Mock
    Order order;

    @BeforeEach
    void setup() {
        Mockito.when(buyer.getUsername()).thenReturn("buyer");
    }

    @Nested
    @DisplayName("주문 요청 테스트")
    class OrderPlacingTest {

        @BeforeEach
        void setup() {
            Mockito.when(memberRepository.findByUsername(any(String.class)))
                    .thenReturn(Optional.of(buyer));

            Mockito.when(voucherRepository.findById(any(Long.class)))
                    .thenReturn(Optional.of(voucher));

            Mockito.when(voucher.getSeller())
                    .thenReturn(seller);
        }

        @Test
        @DisplayName("정상적인 주문 요청 테스트")
        void placeValidOrder() {
            // given
            Mockito.when(voucher.getStatus()).thenReturn(FOR_SALE);
            Mockito.when(buyer.getId()).thenReturn(1L);
            Mockito.when(seller.getId()).thenReturn(2L);
            Mockito.when(orderRepository.save(any(Order.class))).thenReturn(order);
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
            Mockito.when(voucher.getStatus()).thenReturn(SALE_REQUESTED);
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
            Mockito.when(voucher.getStatus()).thenReturn(FOR_SALE);
            Mockito.when(voucher.getSeller().isDeleted()).thenReturn(true);
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
            Mockito.when(voucher.getStatus()).thenReturn(FOR_SALE);
            Mockito.when(voucher.getSeller()).thenReturn(buyer);
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
        void findOrder(@Mock Brand brand, @Mock VoucherKind voucherKind) {
            // given
            List<Voucher> voucherList = List.of(voucher);
            long price = 4_000L;

            Mockito.when(voucherRepository.findAllByOrder(order))
                    .thenReturn(voucherList);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(order.getQuantity()).thenReturn(voucherList.size());
            Mockito.when(order.getAmount()).thenReturn(price * voucherList.size());

            Mockito.when(voucher.getStatus()).thenReturn(ORDER_PLACED);
            Mockito.when(voucher.getPrice()).thenReturn(price);
            Mockito.when(voucher.getVoucherKind()).thenReturn(voucherKind);

            Mockito.when(voucherKind.getBrand()).thenReturn(brand);
            Mockito.when(voucherKind.getImageUrl()).thenReturn("imageUrl");
            Mockito.when(brand.getName()).thenReturn("Brand");

            // when
            OrderSummary orderSummary = orderService.getOrderSummary(order.getOrderNumber(), buyer.getUsername());

            // then
            assertThat(orderSummary.getOrderNumber()).isEqualTo(order.getOrderNumber());
            assertThat(orderSummary.getStatus()).isSameAs(IN_PROGRESS);
            assertThat(orderSummary.getCustomerName()).isEqualTo(buyer.getUsername());
            assertThat(orderSummary.getQuantity()).isEqualTo(voucherList.size());
            assertThat(orderSummary.getTotalPrice()).isEqualTo(voucher.getPrice() * voucherList.size());
            assertThat(orderSummary.getOrderItems().size()).isEqualTo(orderSummary.getQuantity());

            orderSummary.getOrderItems()
                    .forEach(orderItem -> {
                        assertThat(orderItem.getStatus()).isSameAs(ORDER_PLACED);
                        assertThat(orderItem.getVoucherId()).isEqualTo(voucher.getId());
                    });
        }

        @Test
        @DisplayName("주문 조회 예외 1 - VoucherForSale의 status가 ORDER_PLACED가 아닌 경우, 예외를 던진다.")
        void itemOrderNotPlaced() {
            // given
            List<Voucher> voucherList = List.of(voucher);

            Mockito.when(voucherRepository.findAllByOrder(order))
                    .thenReturn(voucherList);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(voucher.getStatus()).thenReturn(SALE_REQUESTED);

            // when & then
            assertThatThrownBy(() -> orderService.getOrderSummary(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ITEM_ORDER_NOT_PLACED.getMessage());
        }

        @Test
        @DisplayName("주문 조회 예외 2 - 주문 정보에 있는 구매자의 username이 사용자의 username과 다르다면 해당 요청을 처리하지 않는다.")
        void buyerNotMatch() {
            // given
            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getBuyer()).thenReturn(buyer);

            // when & then
            assertThatThrownBy(() -> orderService.getOrderSummary(order.getOrderNumber(), "notBuyer"))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(BUYER_NOT_MATCH.getMessage());
        }

        @Test
        @DisplayName("주문 조회 예외 3 - 주문 수량에 오차가 있을 경우, 예외를 던진다.")
        void invalidOrderQuantity(@Mock Brand brand, @Mock VoucherKind voucherKind) {
            // given
            List<Voucher> voucherList = List.of(voucher);

            Mockito.when(voucherRepository.findAllByOrder(order))
                    .thenReturn(voucherList);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getBuyer()).thenReturn(buyer);

            Mockito.when(voucher.getStatus()).thenReturn(ORDER_PLACED);
            Mockito.when(voucher.getPrice()).thenReturn(4_000L);
            Mockito.when(voucher.getVoucherKind()).thenReturn(voucherKind);

            Mockito.when(voucherKind.getBrand()).thenReturn(brand);
            Mockito.when(voucherKind.getImageUrl()).thenReturn("imageUrl");
            Mockito.when(brand.getName()).thenReturn("Brand");

            // when
            Mockito.when(order.getQuantity()).thenReturn(voucherList.size() + 1);

            // then
            assertThatThrownBy(() -> orderService.getOrderSummary(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(INVALID_ORDER_QUANTITY.getMessage());
        }

        @Test
        @DisplayName("주문 조회 예외 4 - 총 주문 금액에 오차가 있을 경우, 예외를 던진다.")
        void invalidOrderAmount(@Mock Brand brand, @Mock VoucherKind voucherKind) {
            // given
            List<Voucher> voucherList = List.of(voucher);
            long price = 4_000L;

            Mockito.when(voucherRepository.findAllByOrder(order))
                    .thenReturn(voucherList);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(order.getQuantity()).thenReturn(voucherList.size());

            Mockito.when(voucher.getStatus()).thenReturn(ORDER_PLACED);
            Mockito.when(voucher.getPrice()).thenReturn(price);
            Mockito.when(voucher.getVoucherKind()).thenReturn(voucherKind);

            Mockito.when(voucherKind.getBrand()).thenReturn(brand);
            Mockito.when(voucherKind.getImageUrl()).thenReturn("imageUrl");
            Mockito.when(brand.getName()).thenReturn("Brand");

            // when
            Mockito.when(order.getAmount()).thenReturn(price * voucherList.size() + 1_000L);

            // then
            assertThatThrownBy(() -> orderService.getOrderSummary(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(INVALID_ORDER_AMOUNT.getMessage());
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
            List<Voucher> voucherList = List.of(voucher);
            long price = 4_000L;

            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(voucherRepository.findAllByOrder(order))
                    .thenReturn(voucherList);

            Mockito.when(voucher.getStatus()).thenReturn(ORDER_PLACED);
            Mockito.when(voucher.getPrice()).thenReturn(price);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getQuantity()).thenReturn(voucherList.size());
            Mockito.when(order.getAmount()).thenReturn(price * voucherList.size());

            // when
            OrderConfirmation orderConfirmation = orderService.confirmOrder(order.getOrderNumber(), buyer.getUsername());

            // then
            assertThat(orderConfirmation.getAmount()).isEqualTo(order.getAmount());
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
        @DisplayName("주문 체결 요청 예외 4 - 주문할 Voucher 중 하나라도 ORDER_PLACED 상태가 아닐 경우, 체결 처리하지 않는다.")
        void itemNotForSaleWhenConfirm() {
            // given
            Mockito.when(order.getBuyer()).thenReturn(buyer);

            Mockito.when(voucherRepository.findAllByOrder(order))
                    .thenReturn(List.of(voucher));

            Mockito.when(voucher.getStatus()).thenReturn(SOLD);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);

            // when & then
            assertThatThrownBy(() -> orderService.confirmOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(ITEM_ORDER_NOT_PLACED.getMessage());
        }

        @Test
        void invalidOrderQuantity() {
            // given
            List<Voucher> voucherList = List.of(voucher);

            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(voucherRepository.findAllByOrder(order))
                    .thenReturn(voucherList);

            Mockito.when(voucher.getStatus()).thenReturn(ORDER_PLACED);
            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);

            // when
            Mockito.when(order.getQuantity()).thenReturn(voucherList.size() + 1);

            // then
            assertThatThrownBy(() -> orderService.confirmOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(INVALID_ORDER_QUANTITY.getMessage());
        }

        @Test
        void invalidOrderAmount() {
            // given
            List<Voucher> voucherList = List.of(voucher);
            long price = 4_000L;

            Mockito.when(order.getBuyer()).thenReturn(buyer);
            Mockito.when(voucherRepository.findAllByOrder(order))
                    .thenReturn(voucherList);

            Mockito.when(voucher.getStatus()).thenReturn(ORDER_PLACED);
            Mockito.when(voucher.getPrice()).thenReturn(price);

            Mockito.when(order.getStatus()).thenReturn(IN_PROGRESS);
            Mockito.when(order.getQuantity()).thenReturn(voucherList.size());

            // when
            Mockito.when(order.getAmount()).thenReturn(0L);

            // then
            assertThatThrownBy(() -> orderService.confirmOrder(order.getOrderNumber(), buyer.getUsername()))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(INVALID_ORDER_AMOUNT.getMessage());
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