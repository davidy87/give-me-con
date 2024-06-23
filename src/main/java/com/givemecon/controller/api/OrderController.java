package com.givemecon.controller.api;

import com.givemecon.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.domain.order.OrderDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderNumberResponse placeOrder(Authentication authentication, @RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest, authentication.getName());
    }

    @GetMapping("/{orderNumber}")
    public OrderSummary findOrder(Authentication authentication, @PathVariable Long orderNumber) {
        return orderService.findOrder(orderNumber, authentication.getName());
    }

    @PutMapping("/{orderNumber}")
    public OrderNumberResponse confirmOrder(Authentication authentication, @PathVariable Long orderNumber) {
        return orderService.confirmOrder(orderNumber, authentication.getName());
    }

    @DeleteMapping("/{orderNumber}")
    public OrderNumberResponse cancelOrder(Authentication authentication, @PathVariable Long orderNumber) {
        return orderService.cancelOrder(orderNumber, authentication.getName());
    }
}
