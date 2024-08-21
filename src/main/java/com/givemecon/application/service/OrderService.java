package com.givemecon.application.service;

import com.givemecon.application.exception.order.InvalidOrderException;
import com.givemecon.common.exception.concrete.EntityNotFoundException;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.order.Order;
import com.givemecon.domain.entity.order.OrderStatus;
import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.OrderRepository;
import com.givemecon.domain.repository.PurchasedVoucherRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.givemecon.application.dto.OrderDto.*;
import static com.givemecon.application.exception.order.OrderErrorCode.*;
import static com.givemecon.domain.entity.order.OrderStatus.CANCELLED;
import static com.givemecon.domain.entity.order.OrderStatus.CONFIRMED;
import static com.givemecon.domain.entity.voucher.VoucherStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    public OrderNumberResponse placeOrder(OrderRequest orderRequest, String username) {
        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        String orderNumber = generateOrderNumber();
        Order order = new Order(orderNumber, buyer);
        int quantity = 0;
        long amount = 0L;

        for (Long id : orderRequest.getVoucherIdList()) {
            Voucher voucher = getValidOrderItem(id, buyer);
            quantity++;
            amount += voucher.getPrice();
            voucher.updateOrder(order);
            voucher.updateStatus(ORDER_PLACED);
        }

        order.updateQuantity(quantity);
        order.updateAmount(amount);

        return new OrderNumberResponse(orderRepository.save(order).getOrderNumber());
    }

    // TODO: 주문번호 생성 로직 변경 필요
    private String generateOrderNumber() {
        return UUID.randomUUID().toString();
    }

    private Voucher getValidOrderItem(Long voucherForSaleId, Member buyer) {
        Voucher voucher = voucherRepository.findById(voucherForSaleId)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        Member seller = voucher.getSeller();

        if (voucher.getStatus() != FOR_SALE) {
            throw new InvalidOrderException(ITEM_NOT_FOR_SALE);
        }

        if (seller == null || seller.isDeleted()) {
            throw new InvalidOrderException(SELLER_UNAVAILABLE);
        }

        if (buyer.getId().equals(seller.getId())) {
            throw new InvalidOrderException(BUYER_EQUALS_SELLER);
        }

        return voucher;
    }

    @Transactional(readOnly = true)
    public OrderSummary getOrderSummary(String orderNumber, String username) {
        Order order = findOrder(orderNumber);

        // order status & buyer 예외 처리
        verifyOrderStatus(order.getStatus());
        verifyBuyer(order.getBuyer(), username);

        int quantity = 0;
        long amount = 0L;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Voucher voucher : voucherRepository.findAllByOrder(order)) {
            if (voucher.getStatus() != ORDER_PLACED) {
                throw new InvalidOrderException(ITEM_ORDER_NOT_PLACED);
            }

            quantity++;
            amount += voucher.getPrice();
            orderItems.add(new OrderItem(voucher));
        }

        // 주문 수량 및 금액 예외 처리
        verifyOrderQuantityAndAmount(order, quantity, amount);

        return OrderSummary.builder()
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .customerName(username)
                .quantity(quantity)
                .totalPrice(amount)
                .orderItems(orderItems)
                .build();
    }

    public OrderConfirmation confirmOrder(String orderNumber, String username) {
        Order order = findOrder(orderNumber);
        Member buyer = order.getBuyer();

        // order status & buyer 예외 처리
        verifyOrderStatus(order.getStatus());
        verifyBuyer(buyer, username);

        int quantity = 0;
        long amount = 0L;
        List<PurchasedVoucher> purchasedVouchers = new ArrayList<>();

        for (Voucher voucher : voucherRepository.findAllByOrder(order)) {
            if (voucher.getStatus() != ORDER_PLACED) {
                throw new InvalidOrderException(ITEM_ORDER_NOT_PLACED);
            }

            quantity++;
            amount += voucher.getPrice();
            voucher.updateStatus(SOLD);
            purchasedVouchers.add(new PurchasedVoucher(voucher, buyer));
        }

        // 주문 수량 및 금액 예외 처리
        verifyOrderQuantityAndAmount(order, quantity, amount);

        purchasedVoucherRepository.saveAll(purchasedVouchers);
        order.updateStatus(CONFIRMED);

        return new OrderConfirmation(order.getAmount());
    }

    public OrderNumberResponse cancelOrder(String orderNumber, String username) {
        Order order = findOrder(orderNumber);

        // order status & buyer 예외 처리
        verifyOrderStatus(order.getStatus());
        verifyBuyer(order.getBuyer(), username);

        order.updateStatus(CANCELLED);
        voucherRepository.updateAllOrderCancelled();
        orderRepository.delete(order);

        return new OrderNumberResponse(orderNumber);
    }

    public Order findOrder(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new InvalidOrderException(INVALID_ORDER_NUMBER));
    }

    private void verifyOrderQuantityAndAmount(Order order, int quantity, long amount) {
        // 주문 수량 예외 처리
        if (order.getQuantity() != quantity) {
            throw new InvalidOrderException(INVALID_ORDER_QUANTITY);
        }

        // 주문금액 예외 처리
        if (order.getAmount() != amount) {
            throw new InvalidOrderException(INVALID_ORDER_AMOUNT);
        }
    }

    public void verifyBuyer(Member buyer, String username) {
        if (buyer == null || !username.equals(buyer.getUsername())) {
            throw new InvalidOrderException(BUYER_NOT_MATCH);
        }
    }

    private void verifyOrderStatus(OrderStatus orderStatus) {
        switch (orderStatus) {
            case CONFIRMED -> throw new InvalidOrderException(ORDER_ALREADY_CONFIRMED);
            case CANCELLED -> throw new InvalidOrderException(ORDER_ALREADY_CANCELLED);
        }
    }
}
