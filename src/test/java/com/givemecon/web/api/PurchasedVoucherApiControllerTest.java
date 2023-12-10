package com.givemecon.web.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.givemecon.web.dto.PurchasedVoucherDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
@WithMockUser(roles = "USER", username = "tester")
class PurchasedVoucherApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void saveAll() throws Exception {
        // given
        Voucher voucherSaved = voucherRepository.save(Voucher.builder()
                .image("voucher.png")
                .title("voucher")
                .price(4_000L)
                .build());

        memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        List<PurchasedVoucherRequest> requestDtoList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            PurchasedVoucherRequest requestDto = PurchasedVoucherRequest.builder()
                    .image("voucher" + i + ".png")
                    .title("voucher" + i)
                    .price(4_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .voucherId(voucherSaved.getId())
                    .build();

            requestDtoList.add(requestDto);
        }

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());
        String url = "http://localhost:" + port + "/api/purchased-vouchers";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(requestDtoList)));

        // then
        response.andExpect(status().isCreated());
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        assertThat(purchasedVoucherList).hasSize(requestDtoList.size());
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        Member owner = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        List<PurchasedVoucher> entityList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            PurchasedVoucher purchasedVoucher = PurchasedVoucher.builder()
                    .image("voucher" + i + ".png")
                    .title("voucher" + i)
                    .price(4_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .build();

            owner.addPurchasedVoucher(purchasedVoucher);
            entityList.add(purchasedVoucher);
        }

        purchasedVoucherRepository.saveAll(entityList);

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());
        String url = "http://localhost:" + port + "/api/purchased-vouchers";

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isOk());
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        assertThat(purchasedVoucherList).hasSize(entityList.size());
        purchasedVoucherList.forEach(purchasedVoucher -> assertThat(purchasedVoucher.getOwner()).isEqualTo(owner));
    }

    @Test
    void findOne() throws Exception {
        // given
        Member owner = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(PurchasedVoucher.builder()
                .image("voucher.png")
                .title("voucher")
                .price(4_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .build());

        owner.addPurchasedVoucher(purchasedVoucher);

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());
        String url = "http://localhost:" + port + "/api/purchased-vouchers/" + purchasedVoucher.getId();

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(purchasedVoucher.getId()))
                .andExpect(jsonPath("image").value(purchasedVoucher.getImage()))
                .andExpect(jsonPath("title").value(purchasedVoucher.getTitle()))
                .andExpect(jsonPath("price").value(purchasedVoucher.getPrice()))
                .andExpect(jsonPath("expDate").value(purchasedVoucher.getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(purchasedVoucher.getBarcode()));
    }
}