package com.givemecon.controller.api;

import com.givemecon.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.domain.order.OrderDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PlacedOrderResponse placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    @GetMapping("/{orderNumber}")
    public OrderSummary findOrder(@PathVariable Long orderNumber) {
        return orderService.findOrder(orderNumber);
    }
}
