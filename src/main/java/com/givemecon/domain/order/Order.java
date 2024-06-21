package com.givemecon.domain.order;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static com.givemecon.domain.order.OrderStatus.*;

@Getter
@SQLDelete(sql = "UPDATE order SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "orders")
@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member buyer;

    public Order() {
        this.status = IN_PROGRESS;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void updateBuyer(Member buyer) {
        this.buyer = buyer;
    }
}
