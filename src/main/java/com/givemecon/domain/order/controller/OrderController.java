package com.givemecon.domain.order.controller;

import com.givemecon.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.domain.order.dto.OrderDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderNumberResponse placeOrder(Authentication authentication,
                                          @Validated @RequestBody OrderRequest orderRequest) {

        return orderService.placeOrder(orderRequest, authentication.getName());
    }

    @GetMapping("/{orderNumber}")
    public OrderSummary findOrder(Authentication authentication, @PathVariable String orderNumber) {
        return orderService.getOrderSummary(orderNumber, authentication.getName());
    }

    @DeleteMapping("/{orderNumber}")
    public OrderNumberResponse cancelOrder(Authentication authentication, @PathVariable String orderNumber) {
        return orderService.cancelOrder(orderNumber, authentication.getName());
    }
}
