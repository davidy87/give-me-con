package com.givemecon.domain.order;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.order.exception.InvalidOrderException;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.givemecon.domain.order.OrderDto.*;
import static com.givemecon.domain.order.OrderStatus.*;
import static com.givemecon.domain.order.exception.OrderErrorCode.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final MemberRepository memberRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    public OrderNumberResponse placeOrder(OrderRequest orderRequest, String username) {
        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        String orderNumber = generateOrderNumber();
        Order order = new Order(orderNumber, buyer);
        int quantity = 0;
        long amount = 0L;

        for (Long id : orderRequest.getVoucherForSaleIdList()) {
            VoucherForSale voucherForSale = getValidOrderItem(id, buyer);
            quantity++;
            amount += voucherForSale.getPrice();
            voucherForSale.updateOrder(order);
            voucherForSale.updateStatus(ORDER_PLACED);
        }

        order.updateQuantity(quantity);
        order.updateAmount(amount);

        return new OrderNumberResponse(orderRepository.save(order).getOrderNumber());
    }

    // TODO: 주문번호 생성 로직 변경 필요
    private String generateOrderNumber() {
        return UUID.randomUUID().toString();
    }

    private VoucherForSale getValidOrderItem(Long voucherForSaleId, Member buyer) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(voucherForSaleId)
                .orElseThrow(() -> new EntityNotFoundException(VoucherForSale.class));

        Member seller = voucherForSale.getSeller();

        if (voucherForSale.getStatus() != FOR_SALE) {
            throw new InvalidOrderException(ITEM_NOT_FOR_SALE);
        }

        if (seller == null || seller.isDeleted()) {
            throw new InvalidOrderException(SELLER_UNAVAILABLE);
        }

        if (buyer.getId().equals(seller.getId())) {
            throw new InvalidOrderException(BUYER_EQUALS_SELLER);
        }

        return voucherForSale;
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

        for (VoucherForSale voucherForSale : voucherForSaleRepository.findAllByOrder(order)) {
            if (voucherForSale.getStatus() != ORDER_PLACED) {
                throw new InvalidOrderException(ITEM_ORDER_NOT_PLACED);
            }

            quantity++;
            amount += voucherForSale.getPrice();
            orderItems.add(new OrderItem(voucherForSale));
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

        for (VoucherForSale voucherForSale : voucherForSaleRepository.findAllByOrder(order)) {
            if (voucherForSale.getStatus() != ORDER_PLACED) {
                throw new InvalidOrderException(ITEM_ORDER_NOT_PLACED);
            }

            quantity++;
            amount += voucherForSale.getPrice();
            voucherForSale.updateStatus(SOLD);
            purchasedVouchers.add(new PurchasedVoucher(voucherForSale, buyer));
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
        voucherForSaleRepository.updateAllOrderCancelled();
        orderRepository.delete(order);

        return new OrderNumberResponse(orderNumber);
    }

    private Order findOrder(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException(Order.class));
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

    private void verifyBuyer(Member buyer, String username) {
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
