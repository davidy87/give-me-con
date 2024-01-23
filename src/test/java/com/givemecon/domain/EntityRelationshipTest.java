package com.givemecon.domain;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandIcon;
import com.givemecon.domain.brand.BrandIconRepository;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryIcon;
import com.givemecon.domain.category.CategoryIconRepository;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.domain.likedvoucher.LikedVoucher;
import com.givemecon.domain.likedvoucher.LikedVoucherRepository;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
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
    CategoryIconRepository categoryIconRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    BrandIconRepository brandIconRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    LikedVoucherRepository likedVoucherRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Test
    void categoryAndCategoryIcon() {
        // given
        Category category = categoryRepository.save(Category.builder()
                .name("coffee")
                .build());

        CategoryIcon icon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .originalName("testIcon.png")
                .imageUrl("imageUrl")
                .build());

        // when
        category.setCategoryIcon(icon);

        // then
        CategoryIcon found = category.getCategoryIcon();
        assertThat(found).isEqualTo(icon);
    }

    @Test
    void brandAndBrandIcon() {
        // given
        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());

        BrandIcon icon = brandIconRepository.save(BrandIcon.builder()
                .imageKey("imageKey")
                .originalName("testIcon.png")
                .imageUrl("imageUrl")
                .build());

        // when
        brand.setBrandIcon(icon);

        // then
        BrandIcon found = brand.getBrandIcon();
        assertThat(found).isEqualTo(icon);
    }

    @Test
    void brand() {
        // given
        Category category = Category.builder()
                .name("coffee")
                .build();

        Brand brand = Brand.builder()
                .name("Starbucks")
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
                .build();

        Brand brand = Brand.builder()
                .name("Starbucks")
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
    void likedVoucher() {
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

        LikedVoucher likedVoucher = LikedVoucher.builder()
                .voucher(voucherSaved)
                .build();

        LikedVoucher likedVoucherSaved = likedVoucherRepository.save(likedVoucher);
        memberSaved.addLikedVoucher(likedVoucherSaved);

        // when
        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();

        // then
        LikedVoucher found = likedVoucherList.get(0);
        assertThat(found.getMember()).isEqualTo(memberSaved);
        assertThat(found.getVoucher()).isEqualTo(voucherSaved);
    }

    @Test
    void purchasedVoucher() {
        // given
        Category category = Category.builder()
                .name("coffee")
                .build();

        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        Member owner = Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        PurchasedVoucher purchasedVoucher = PurchasedVoucher.builder()
                .title("voucher")
                .price(4_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .image("voucher.png")
                .build();

        Category categorySaved = categoryRepository.save(category);
        Brand brandSaved = brandRepository.save(brand);
        Member ownerSaved = memberRepository.save(owner);
        PurchasedVoucher purchasedVoucherSaved = purchasedVoucherRepository.save(purchasedVoucher);

        // when
        purchasedVoucherSaved.setCategory(categorySaved);
        purchasedVoucherSaved.setBrand(brandSaved);
        ownerSaved.addPurchasedVoucher(purchasedVoucherSaved);
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getCategory()).isEqualTo(categorySaved);
        assertThat(found.getBrand()).isEqualTo(brandSaved);
        assertThat(found.getOwner()).isEqualTo(ownerSaved);
    }
}
