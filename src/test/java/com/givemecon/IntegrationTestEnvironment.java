package com.givemecon;

import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.OrderRepository;
import com.givemecon.domain.repository.PaymentRepository;
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
import com.givemecon.util.MockBeanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Import(MockBeanConfig.class)
@Transactional
@SpringBootTest
public abstract class IntegrationTestEnvironment {

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
    protected PurchasedVoucherRepository purchasedVoucherRepository;

    @Autowired
    protected LikedVoucherRepository likedVoucherRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected PaymentRepository paymentRepository;

    @Autowired
    protected JwtTokenService jwtTokenService;
}
