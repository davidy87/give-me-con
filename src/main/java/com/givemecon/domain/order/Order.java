package com.givemecon.domain.order;

import com.givemecon.domain.BaseTimeEntity;
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
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member buyer;

    public Order(String orderNumber, Member buyer) {
        this.orderNumber = orderNumber;
        this.buyer = buyer;
        this.status = IN_PROGRESS;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}
