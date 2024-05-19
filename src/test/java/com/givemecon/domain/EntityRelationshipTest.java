package com.givemecon.domain;

import com.givemecon.config.enums.Authority;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.image.brand.BrandIcon;
import com.givemecon.domain.image.brand.BrandIconRepository;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.image.category.CategoryIcon;
import com.givemecon.domain.image.category.CategoryIconRepository;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
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
        category.updateCategoryIcon(icon);

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
        brand.updateBrandIcon(icon);

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
        brand.updateCategory(categorySaved);
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

        Voucher voucher = Voucher.builder()
                .title("Starbucks Americano T")
                .build();

        Category categorySaved = categoryRepository.save(category);
        brand.updateCategory(categorySaved);
        Brand brandSaved = brandRepository.save(brand);

        // when
        voucher.updateCategory(categorySaved);
        voucher.updateBrand(brandSaved);
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
        Member seller = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .authority(Authority.USER)
                .build());

        VoucherForSale voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                .price(15_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build());

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("Americano T")
                .build());

        // when
        voucherForSale.updateSeller(seller);
        voucherForSale.updateVoucher(voucher);

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
                .authority(Authority.USER)
                .build());

        Voucher voucherSaved = voucherRepository.save(Voucher.builder()
                .title("Americano T")
                .build());

        LikedVoucher likedVoucher = LikedVoucher.builder()
                .voucher(voucherSaved)
                .build();

        LikedVoucher likedVoucherSaved = likedVoucherRepository.save(likedVoucher);
        likedVoucherSaved.updateMember(memberSaved);

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
        Category category = categoryRepository.save(Category.builder()
                .name("coffee")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());

        Member owner = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .authority(Authority.USER)
                .build());

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .build());

        VoucherForSale voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now().plusDays(1))
                .build());

        category.addBrand(brand);
        brand.addVoucher(voucher);
        voucher.updateCategory(category);
        voucherForSale.updateVoucher(voucher);
        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(new PurchasedVoucher());

        // when
        purchasedVoucher.updateVoucherForSale(voucherForSale);
        purchasedVoucher.updateOwner(owner);
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getOwner()).isEqualTo(owner);
        assertThat(found.getVoucherForSale()).isEqualTo(voucherForSale);
        assertThat(found.getVoucherForSale().getVoucher()).isEqualTo(voucher);
        assertThat(found.getVoucherForSale().getVoucher().getCategory()).isEqualTo(category);
        assertThat(found.getVoucherForSale().getVoucher().getBrand()).isEqualTo(brand);
    }
}
