package com.givemecon;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherForSale;
import com.givemecon.domain.voucher.VoucherForSaleRepository;
import com.givemecon.domain.voucher.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@RequiredArgsConstructor
@Component
public class TestSetup {

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

//    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void setup() {
        String[] categories = {"카페", "베이커리", "치킨", "피자", "버거", "아이스크림", "편의점", "뷰티"};

        Member member = Member.builder()
                .email("admin@gmail.com")
                .username("admin")
                .password(passwordEncoder.encode("testpass!"))
                .role(Role.ADMIN)
                .build();

        memberRepository.save(member);

        for (String categoryName : categories) {
            Category category = Category.builder()
                    .name(categoryName)
                    .build();

            Category categorySaved = categoryRepository.save(category);

            for (int i = 1; i <= 10; i++) {
                Brand brand = Brand.builder()
                        .name(categoryName + "_Brand_" + i)
                        .icon(categoryName + "_Brand_" + i + ".png")
                        .build();

                Brand brandSaved = brandRepository.save(brand);
                categorySaved.addBrand(brandSaved);

                for (int j = 1; j <= 10; j++) {
                    Voucher voucher = Voucher.builder()
                            .price(4_000L)
                            .title("Voucher " + j)
                            .image("voucher_" + j + ".png")
                            .build();

                    Voucher voucherSaved = voucherRepository.save(voucher);
                    voucherSaved.setCategory(categorySaved);
                    brandSaved.addVoucher(voucherSaved);

                    for (int k = 1; k <= 5; k++) {
                        VoucherForSale voucherForSale = VoucherForSale.builder()
                                .title(voucherSaved.getTitle())
                                .price(4_000L * k)
                                .expDate(LocalDate.now())
                                .barcode("1111 1111 1111")
                                .image("voucherForSale_" + k)
                                .build();

                        voucherSaved.addVoucherForSale(voucherForSaleRepository.save(voucherForSale));
                    }
                }
            }
        }
    }
}
