package com.givemecon.domain;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.voucher.VoucherSelling;
import com.givemecon.domain.voucher.VoucherSellingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
public class EntityRelationshipTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherSellingRepository voucherSellingRepository;

    @Test
    void brand() {
        // given
        Category category = Category.builder()
                .name("coffee")
                .icon("coffee.jpg")
                .build();

        Brand brand = Brand.builder()
                .name("Starbucks")
                .icon("starbucks.jpg")
                .build();

        Category categorySaved = categoryRepository.save(category);

        // when
        brand.setCategory(categorySaved);
        brandRepository.save(brand);
        List<Brand> brandList = brandRepository.findAll();

        // then
        Brand found = brandList.get(0);
        assertThat(found.getCategory()).isEqualTo(categorySaved);
    }

    @Test
    void voucher() {
        // given
        Category category = Category.builder()
                .name("coffee")
                .icon("coffee.jpg")
                .build();

        Brand brand = Brand.builder()
                .name("Starbucks")
                .icon("starbucks.jpg")
                .build();

        Member seller = Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Voucher voucher = Voucher.builder()
                .title("Starbucks Americano T")
                .price(15_000L)
                .image("americano.jpg")
                .build();

        Category categorySaved = categoryRepository.save(category);
        brand.setCategory(categorySaved);
        Brand brandSaved = brandRepository.save(brand);

        // when
        voucher.setCategory(categorySaved);
        voucher.setBrand(brandSaved);
        voucherRepository.save(voucher);
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        Voucher found = voucherList.get(0);
        assertThat(found.getCategory()).isEqualTo(categorySaved);
        assertThat(found.getBrand()).isEqualTo(brandSaved);
    }

    @Test
    void voucherSelling() {
        // given
        Member seller = Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        VoucherSelling voucherSelling = VoucherSelling.builder()
                .title("Americano T")
                .price(15_000L)
                .expDate(LocalDate.now())
                .image("americano_T.png")
                .build();

        Voucher voucher = Voucher.builder()
                .title("Americano T")
                .price(15_000L)
                .image("americano.jpg")
                .build();

        Member sellerSaved = memberRepository.save(seller);
        Voucher voucherSaved = voucherRepository.save(voucher);

        // when
        voucherSelling.setVoucher(voucherSaved);
        voucherSelling.setSeller(sellerSaved);
        voucherSellingRepository.save(voucherSelling);

        // then
        List<VoucherSelling> voucherSellingList = voucherSellingRepository.findAll();
        assertThat(voucherSellingList.get(0).getSeller()).isEqualTo(seller);
        assertThat(voucherSellingList.get(0).getVoucher()).isEqualTo(voucher);
    }
}
