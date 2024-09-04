package com.givemecon.domain.entity.payment;

import com.givemecon.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE payment SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(nullable = false)
    private String receiptUrl;

    @Embedded
    private OrderInfo orderInfo;

    @Builder
    public Payment(String paymentKey, PaymentMethod method, String receiptUrl, OrderInfo orderInfo) {
        this.paymentKey = paymentKey;
        this.method = method;
        this.receiptUrl = receiptUrl;
        this.orderInfo = orderInfo;
    }
}
