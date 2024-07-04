package com.givemecon.domain.order;

import com.givemecon.domain.BaseEntity;
import com.givemecon.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static com.givemecon.domain.order.OrderStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member buyer;

    public Order(String orderNumber, Member buyer) {
        this.orderNumber = orderNumber;
        this.buyer = buyer;
        this.quantity = 0;
        this.amount = 0L;
        this.status = IN_PROGRESS;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateAmount(Long amount) {
        this.amount = amount;
    }
}
