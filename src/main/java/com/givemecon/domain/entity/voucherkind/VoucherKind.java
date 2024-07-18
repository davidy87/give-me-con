package com.givemecon.domain.entity.voucherkind;

import com.givemecon.domain.entity.BaseEntity;
import com.givemecon.domain.entity.brand.Brand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE voucher_kind SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class VoucherKind extends BaseEntity {

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
    private VoucherKindImage voucherKindImage;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brand brand;

    @Builder
    public VoucherKind(String title,
                       String description,
                       String caution,
                       VoucherKindImage voucherKindImage,
                       Brand brand) {

        this.title = title;
        this.description = description;
        this.caution = caution;
        this.voucherKindImage = voucherKindImage;
        this.brand = brand;
    }

    public String getImageUrl() {
        return voucherKindImage.getImageUrl();
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
}
