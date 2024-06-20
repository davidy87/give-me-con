package com.givemecon.domain.order;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.order.exception.InvalidOrderException;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.givemecon.domain.order.OrderDto.*;
import static com.givemecon.domain.order.OrderStatus.IN_PROGRESS;
import static com.givemecon.domain.order.exception.OrderErrorCode.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final MemberRepository memberRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    public PlacedOrderResponse placeOrder(OrderRequest orderRequest) {
        Member buyer = memberRepository.findById(orderRequest.getBuyerId())
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Order order = orderRepository.save(new Order());
        order.updateBuyer(buyer);

        orderRequest.getVoucherForSaleIdList().forEach(id -> {
            VoucherForSale voucherForSale = getValidOrderItem(id);
            voucherForSale.updateOrder(order);
        });

        return new PlacedOrderResponse(order.getId());
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

    public OrderSummary findOrder(Long orderNumber) {
        Order order = orderRepository.findById(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException(Order.class));

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
}
