package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.BaseTimeEntity;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PurchasedVoucher extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private LocalDate expDate;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false, length = 500)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @Builder
    public PurchasedVoucher(String title, Long price, LocalDate expDate, String barcode, String image) {
        this.title = title;
        this.price = price;
        this.expDate = expDate;
        this.barcode = barcode;
        this.image = image;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setOwner(Member owner) {
        this.owner = owner;
    }
}
