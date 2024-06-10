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
import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.image.voucher.VoucherImageRepository;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImageRepository;
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
    VoucherImageRepository voucherImageRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    VoucherForSaleImageRepository voucherForSaleImageRepository;

    @Autowired
    LikedVoucherRepository likedVoucherRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Test
    void categoryIcon() {
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
        List<Category> categoryList = categoryRepository.findAll();

        // then
        assertThat(categoryList).isNotEmpty();
        assertThat(categoryList.get(0).getCategoryIcon()).isEqualTo(icon);
    }

    @Test
    void brandIcon() {
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
        List<Brand> brandList = brandRepository.findAll();

        // then
        assertThat(brandList).isNotEmpty();
        assertThat(brandList.get(0).getBrandIcon()).isEqualTo(icon);
    }

    @Test
    void voucherImage() {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("Americano T")
                .description("description")
                .caution("caution")
                .build());

        VoucherImage image = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .originalName("testIcon.png")
                .imageUrl("imageUrl")
                .build());

        // when
        voucher.updateVoucherImage(image);
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        assertThat(voucherList).isNotEmpty();
        assertThat(voucherList.get(0).getVoucherImage()).isEqualTo(image);
    }

    @Test
    void voucherForSaleImage() {
        // given
        VoucherForSale voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .build());

        VoucherForSaleImage image = voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                .imageKey("imageKey")
                .originalName("testIcon.png")
                .imageUrl("imageUrl")
                .build());

        // when
        voucherForSale.updateVoucherForSaleImage(image);
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();

        // then
        assertThat(voucherForSaleList).isNotEmpty();
        assertThat(voucherForSaleList.get(0).getVoucherForSaleImage()).isEqualTo(image);
    }

    @Test
    void brand() {
        // given
        Category category = categoryRepository.save(Category.builder()
                .name("coffee")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());

        // when
        brand.updateCategory(category);
        List<Brand> brandList = brandRepository.findAll();

        // then
        assertThat(brandList).isNotEmpty();
        assertThat(brandList.get(0).getCategory()).isEqualTo(category);
    }

    @Test
    void voucher() {
        // given
        Category category = categoryRepository.save(Category.builder()
                .name("coffee")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("Starbucks Americano T")
                .build());

        // when
        brand.updateCategory(category);
        voucher.updateBrand(brand);
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        assertThat(voucherList).isNotEmpty();

        Voucher found = voucherList.get(0);
        assertThat(found.getBrand()).isEqualTo(brand);
        assertThat(found.getBrand().getCategory()).isEqualTo(category);
    }

    @Test
    void voucherForSale() {
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
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();

        // then
        assertThat(voucherForSaleList).isNotEmpty();

        VoucherForSale found = voucherForSaleList.get(0);
        assertThat(found.getSeller()).isEqualTo(seller);
        assertThat(found.getVoucher()).isEqualTo(voucher);
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

        likedVoucherRepository.save(LikedVoucher.builder()
                .member(memberSaved)
                .voucher(voucherSaved)
                .build());

        // when
        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();

        // then
        assertThat(likedVoucherList).isNotEmpty();

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


        // when
        brand.updateCategory(category);
        voucher.updateBrand(brand);
        voucherForSale.updateVoucher(voucher);
        purchasedVoucherRepository.save(new PurchasedVoucher(voucherForSale, owner));

        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        assertThat(purchasedVoucherList).isNotEmpty();

        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getOwner()).isEqualTo(owner);
        assertThat(found.getVoucherForSale()).isEqualTo(voucherForSale);
        assertThat(found.getVoucherForSale().getVoucher()).isEqualTo(voucher);
        assertThat(found.getVoucherForSale().getVoucher().getBrand()).isEqualTo(brand);
        assertThat(found.getVoucherForSale().getVoucher().getBrand().getCategory()).isEqualTo(category);
    }
}
