package com.givemecon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(RestDocumentationExtension.class)
@Transactional
@SpringBootTest
public abstract class IntegrationTest {

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

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();
    }
}
