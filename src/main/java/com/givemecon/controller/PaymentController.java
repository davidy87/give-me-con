package com.givemecon.controller;

import com.givemecon.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.application.dto.PaymentDto.PaymentRequest;
import static com.givemecon.application.dto.PaymentDto.PaymentResponse;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/confirm")
    public PaymentResponse confirmPayment(Authentication authentication,
                                          @Validated @RequestBody PaymentRequest paymentRequest) {

        return paymentService.confirmPayment(paymentRequest, authentication.getName());
    }

    @GetMapping("/{paymentKey}")
    public PaymentResponse findPaymentHistory(Authentication authentication, @PathVariable String paymentKey) {
        return paymentService.findPaymentHistory(paymentKey, authentication.getName());
    }
}
