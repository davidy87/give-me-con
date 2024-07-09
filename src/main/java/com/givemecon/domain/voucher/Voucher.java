package com.givemecon.domain.voucher;

import com.givemecon.domain.BaseEntity;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.image.voucher.VoucherImage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE voucher SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class Voucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String caution;

    @OneToOne
    private VoucherImage voucherImage;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brand brand;

    @Builder
    public Voucher(String title, String description, String caution) {
        this.title = title;
        this.description = description;
        this.caution = caution;
    }

    public String getImageUrl() {
        return voucherImage.getImageUrl();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateCaution(String caution) {
        this.caution = caution;
    }

    public void updateVoucherImage(VoucherImage voucherImage) {
        this.voucherImage = voucherImage;
    }

    public void updateBrand(Brand brand) {
        this.brand = brand;
    }
}
