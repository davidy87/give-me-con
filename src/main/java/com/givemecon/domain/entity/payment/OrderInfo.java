package com.givemecon.domain.entity.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class OrderInfo {

    @Column(unique = true, nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private Long amount;

    public OrderInfo(String orderId, String orderName, Long amount) {
        this.orderId = orderId;
        this.orderName = orderName;
        this.amount = amount;
    }
}
