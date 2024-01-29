package com.givemecon.domain.voucher;

import com.givemecon.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoucherImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String imageKey;

    @Column(nullable = false, unique = true)
    private String imageUrl;

    @Column(nullable = false)
    private String originalName;

    @Builder
    public VoucherImage(String imageKey, String imageUrl, String originalName) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.originalName = originalName;
    }

    public void update(String imageUrl, String originalName) {
        this.imageUrl = imageUrl;
        this.originalName = originalName;
    }
}
