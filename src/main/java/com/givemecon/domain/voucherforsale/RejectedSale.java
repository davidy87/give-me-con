package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.BaseTimeEntity;
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
public class RejectedSale extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long voucherForSaleId;

    @Column(nullable = false)
    private String rejectedReason;

    @Builder
    public RejectedSale(Long voucherForSaleId, String rejectedReason) {
        this.voucherForSaleId = voucherForSaleId;
        this.rejectedReason = rejectedReason;
    }
}
