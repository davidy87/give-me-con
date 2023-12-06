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
import java.util.List;

import static com.givemecon.web.dto.PurchasedVoucherDto.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
    void savePurchasedVoucher() throws Exception {
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

        PurchasedVoucherRequest requestDto = PurchasedVoucherRequest.builder()
                .image("voucher.png")
                .title("voucher")
                .price(4_000L)
                .expDate(LocalDate.now())
                .barcode("1111 1111 1111")
                .voucherId(voucherSaved.getId())
                .build();

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());
        String url = "http://localhost:" + port + "/api/purchased";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(requestDto)));

        // then
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();
        PurchasedVoucher found = purchasedVoucherList.get(0);

        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(found.getId()))
                .andExpect(jsonPath("image").value(found.getImage()))
                .andExpect(jsonPath("title").value(found.getTitle()))
                .andExpect(jsonPath("price").value(found.getPrice()))
                .andExpect(jsonPath("expDate").value(found.getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(found.getBarcode()));
    }
}