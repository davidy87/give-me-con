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
import com.givemecon.domain.voucher.VoucherForSale;
import com.givemecon.domain.voucher.VoucherForSaleRepository;
import com.givemecon.domain.voucherliked.VoucherLiked;
import com.givemecon.domain.voucherliked.VoucherLikedRepository;
import com.givemecon.domain.voucherpurchased.VoucherPurchased;
import com.givemecon.domain.voucherpurchased.VoucherPurchasedRepository;
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
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    VoucherLikedRepository voucherLikedRepository;

    @Autowired
    VoucherPurchasedRepository voucherPurchasedRepository;

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

        VoucherForSale voucherForSale = VoucherForSale.builder()
                .title("Americano T")
                .price(15_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
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
        voucherForSale.setVoucher(voucherSaved);
        voucherForSale.setSeller(sellerSaved);
        voucherForSaleRepository.save(voucherForSale);

        // then
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();
        assertThat(voucherForSaleList.get(0).getSeller()).isEqualTo(seller);
        assertThat(voucherForSaleList.get(0).getVoucher()).isEqualTo(voucher);
    }

    @Test
    void voucherLiked() {
        // given
        Member memberSaved = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        Voucher voucherSaved = voucherRepository.save(Voucher.builder()
                .title("Americano T")
                .price(15_000L)
                .image("americano.jpg")
                .build());

        VoucherLiked voucherLiked = VoucherLiked.builder()
                .voucher(voucherSaved)
                .member(memberSaved)
                .build();

        VoucherLiked voucherLikedSaved = voucherLikedRepository.save(voucherLiked);

        // when
        List<VoucherLiked> voucherLikedList = voucherLikedRepository.findAll();

        // then
        VoucherLiked found = voucherLikedList.get(0);
        assertThat(found.getMember()).isEqualTo(memberSaved);
        assertThat(found.getVoucher()).isEqualTo(voucherSaved);
    }

    @Test
    void VoucherPurchased() {
        // given
        Category category = Category.builder()
                .name("coffee")
                .icon("coffee.jpg")
                .build();

        Brand brand = Brand.builder()
                .name("Starbucks")
                .icon("starbucks.jpg")
                .build();

        Member owner = Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        VoucherPurchased voucherPurchased = VoucherPurchased.builder()
                .title("voucher")
                .price(4_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .image("voucher.png")
                .build();

        Category categorySaved = categoryRepository.save(category);
        Brand brandSaved = brandRepository.save(brand);
        Member ownerSaved = memberRepository.save(owner);
        VoucherPurchased voucherPurchasedSaved = voucherPurchasedRepository.save(voucherPurchased);

        // when
        voucherPurchasedSaved.setCategory(categorySaved);
        voucherPurchasedSaved.setBrand(brandSaved);
        voucherPurchasedSaved.setOwner(ownerSaved);
        List<VoucherPurchased> voucherPurchasedList = voucherPurchasedRepository.findAll();

        // then
        VoucherPurchased found = voucherPurchasedList.get(0);
        assertThat(found.getCategory()).isEqualTo(categorySaved);
        assertThat(found.getBrand()).isEqualTo(brandSaved);
        assertThat(found.getOwner()).isEqualTo(ownerSaved);
    }
}
