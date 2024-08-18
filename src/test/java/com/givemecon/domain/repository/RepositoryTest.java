package com.givemecon.domain.repository;

import com.givemecon.common.configuration.JpaConfig;
import com.givemecon.domain.repository.brand.BrandIconRepository;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import com.givemecon.domain.repository.likedvoucher.LikedVoucherRepository;
import com.givemecon.domain.repository.voucher.RejectedSaleRepository;
import com.givemecon.domain.repository.voucher.VoucherImageRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaConfig.class)
@DataJpaTest
public abstract class RepositoryTest {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected CategoryIconRepository categoryIconRepository;

    @Autowired
    protected BrandRepository brandRepository;

    @Autowired
    protected BrandIconRepository brandIconRepository;

    @Autowired
    protected VoucherKindRepository voucherKindRepository;

    @Autowired
    protected VoucherKindImageRepository voucherKindImageRepository;

    @Autowired
    protected VoucherRepository voucherRepository;

    @Autowired
    protected VoucherImageRepository voucherImageRepository;

    @Autowired
    protected RejectedSaleRepository rejectedSaleRepository;

    @Autowired
    protected PurchasedVoucherRepository purchasedVoucherRepository;

    @Autowired
    protected LikedVoucherRepository likedVoucherRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected PaymentRepository paymentRepository;
}
