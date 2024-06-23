package com.givemecon.domain.order;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.order.exception.InvalidOrderException;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        Order order = orderRepository.save(new Order());
        order.updateBuyer(buyer);

        orderRequest.getVoucherForSaleIdList().forEach(id -> {
            VoucherForSale voucherForSale = getValidOrderItem(id);
            voucherForSale.updateOrder(order);
        });

        return new OrderNumberResponse(order.getId());
    }

    private VoucherForSale getValidOrderItem(Long voucherForSaleId) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(voucherForSaleId)
                .orElseThrow(() -> new EntityNotFoundException(VoucherForSale.class));

        Member seller = voucherForSale.getSeller();

        if (voucherForSale.getStatus() != FOR_SALE) {
            throw new InvalidOrderException(ITEM_NOT_FOR_SALE);
        }

        if (seller == null || seller.isDeleted()) {
            throw new InvalidOrderException(SELLER_UNAVAILABLE);
        }

        return voucherForSale;
    }

    public OrderSummary findOrder(Long orderNumber, String username) {
        Order order = orderRepository.findById(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException(Order.class));

        Member buyer = order.getBuyer();

        if (!username.equals(buyer.getUsername())) {
            throw new InvalidOrderException(BUYER_NOT_MATCH);
        }

        int quantity = 0;
        long totalPrice = 0L;
        List<OrderItem> orderItems = new ArrayList<>();

        for (VoucherForSale voucherForSale : voucherForSaleRepository.findAllByOrder(order)) {
            if (order.getStatus() == IN_PROGRESS && voucherForSale.getStatus() != FOR_SALE) {
                continue;
            }

            quantity++;
            totalPrice += voucherForSale.getPrice();
            orderItems.add(new OrderItem(voucherForSale));
        }

        return new OrderSummary(order.getStatus(), quantity, totalPrice, orderItems);
    }

    public OrderNumberResponse confirmOrder(Long orderNumber, String username) {
        Order order = orderRepository.findById(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException(Order.class));

        if (order.getStatus() == CONFIRMED) {
            throw new InvalidOrderException(ORDER_ALREADY_CONFIRMED);
        }

        Member buyer = order.getBuyer();

        if (!username.equals(buyer.getUsername())) {
            throw new InvalidOrderException(BUYER_NOT_MATCH);
        }

        List<PurchasedVoucher> purchasedVouchers =
                voucherForSaleRepository.findAllByOrder(order).stream()
                        .filter(voucherForSale -> voucherForSale.getStatus() == FOR_SALE)
                        .map(voucherForSale -> {
                            voucherForSale.updateStatus(SOLD);
                            return new PurchasedVoucher(voucherForSale, buyer);
                        })
                        .toList();

        purchasedVoucherRepository.saveAll(purchasedVouchers);
        order.updateStatus(CONFIRMED);

        return new OrderNumberResponse(orderNumber);
    }

    public OrderNumberResponse cancelOrder(Long orderNumber, String username) {
        Order order = orderRepository.findById(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException(Order.class));

        if (order.getStatus() == CONFIRMED) {
            throw new InvalidOrderException(ORDER_ALREADY_CONFIRMED);
        }

        if (order.getStatus() == CANCELLED) {
            throw new InvalidOrderException(ORDER_ALREADY_CANCELLED);
        }

        if (!username.equals(order.getBuyer().getUsername())) {
            throw new InvalidOrderException(BUYER_NOT_MATCH);
        }

        order.updateStatus(CANCELLED);
        voucherForSaleRepository.updateAllOrderCancelled();
        orderRepository.delete(order);

        return new OrderNumberResponse(orderNumber);
    }
}
