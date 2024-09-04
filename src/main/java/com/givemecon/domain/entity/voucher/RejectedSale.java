package com.givemecon.domain.entity.voucher;

import com.givemecon.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE rejected_sale SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class RejectedSale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long voucherId;

    @Column(nullable = false)
    private String rejectedReason;

    @Builder
    public RejectedSale(Long voucherId, String rejectedReason) {
        this.voucherId = voucherId;
        this.rejectedReason = rejectedReason;
    }
}
