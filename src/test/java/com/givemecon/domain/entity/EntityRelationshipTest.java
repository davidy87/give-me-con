package com.givemecon.domain.entity;

import com.givemecon.domain.entity.member.Authority;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.entity.likedvoucher.LikedVoucher;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.PurchasedVoucherRepository;
import com.givemecon.domain.repository.brand.BrandIconRepository;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import com.givemecon.domain.repository.likedvoucher.LikedVoucherRepository;
import com.givemecon.domain.repository.voucher.VoucherImageRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
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
    VoucherKindRepository voucherKindRepository;

    @Autowired
    VoucherKindImageRepository voucherKindImageRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherImageRepository voucherImageRepository;

    @Autowired
    LikedVoucherRepository likedVoucherRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Test
    void categoryIcon() {
        // given
        CategoryIcon categoryIcon = CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        Category category = Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build();

        categoryIconRepository.save(categoryIcon);
        categoryRepository.save(category);

        // when
        List<Category> categoryList = categoryRepository.findAll();

        // then
        assertThat(categoryList).isNotEmpty();
        assertThat(categoryList.get(0).getCategoryIcon()).isEqualTo(categoryIcon);
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
        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("Americano T")
                .description("description")
                .caution("caution")
                .build());

        VoucherKindImage image = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .originalName("testIcon.png")
                .imageUrl("imageUrl")
                .build());

        // when
        voucherKind.updateVoucherKindImage(image);
        List<VoucherKind> voucherKindList = voucherKindRepository.findAll();

        // then
        assertThat(voucherKindList).isNotEmpty();
        assertThat(voucherKindList.get(0).getVoucherKindImage()).isEqualTo(image);
    }

    @Test
    void voucherForSaleImage() {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
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
    void brand() {
        // given
        CategoryIcon categoryIcon = CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        Category category = Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build();

        categoryIconRepository.save(categoryIcon);
        categoryRepository.save(category);

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
    void voucherKind() {
        // given
        CategoryIcon categoryIcon = CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        Category category = Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build();

        categoryIconRepository.save(categoryIcon);
        categoryRepository.save(category);

        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());

        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("Starbucks Americano T")
                .build());

        // when
        brand.updateCategory(category);
        voucherKind.updateBrand(brand);
        List<VoucherKind> voucherKindList = voucherKindRepository.findAll();

        // then
        assertThat(voucherKindList).isNotEmpty();

        VoucherKind found = voucherKindList.get(0);
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

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(15_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build());

        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("Americano T")
                .build());

        // when
        voucher.updateSeller(seller);
        voucher.updateVoucherKind(voucherKind);
        List<Voucher> voucherList = voucherRepository.findAll();

        // then
        assertThat(voucherList).isNotEmpty();

        Voucher found = voucherList.get(0);
        assertThat(found.getSeller()).isEqualTo(seller);
        assertThat(found.getVoucherKind()).isEqualTo(voucherKind);
    }

    @Test
    void likedVoucher() {
        // given
        Member memberSaved = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .authority(Authority.USER)
                .build());

        VoucherKind voucherKindSaved = voucherKindRepository.save(VoucherKind.builder()
                .title("Americano T")
                .build());

        likedVoucherRepository.save(LikedVoucher.builder()
                .member(memberSaved)
                .voucherKind(voucherKindSaved)
                .build());

        // when
        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();

        // then
        assertThat(likedVoucherList).isNotEmpty();

        LikedVoucher found = likedVoucherList.get(0);
        assertThat(found.getMember()).isEqualTo(memberSaved);
        assertThat(found.getVoucherKind()).isEqualTo(voucherKindSaved);
    }

    @Test
    void purchasedVoucher() {
        // given
        CategoryIcon categoryIcon = CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        Category category = Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build();

        categoryIconRepository.save(categoryIcon);
        categoryRepository.save(category);

        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());

        Member owner = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .authority(Authority.USER)
                .build());

        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .build());

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now().plusDays(1))
                .build());


        // when
        brand.updateCategory(category);
        voucherKind.updateBrand(brand);
        voucher.updateVoucherKind(voucherKind);
        purchasedVoucherRepository.save(new PurchasedVoucher(voucher, owner));

        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        assertThat(purchasedVoucherList).isNotEmpty();

        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getOwner()).isEqualTo(owner);
        assertThat(found.getVoucher()).isEqualTo(voucher);
        assertThat(found.getVoucher().getVoucherKind()).isEqualTo(voucherKind);
        assertThat(found.getVoucher().getVoucherKind().getBrand()).isEqualTo(brand);
        assertThat(found.getVoucher().getVoucherKind().getBrand().getCategory()).isEqualTo(category);
    }
}
