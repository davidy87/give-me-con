package com.givemecon.domain.voucher;

import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor
@Entity
public class Voucher extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String image;

    @Builder
    public Voucher(Long price, String image) {
        this.price = price;
        this.image = image;
    }
}
