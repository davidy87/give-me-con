package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.likedvoucher.LikedVoucher;
import com.givemecon.domain.likedvoucher.LikedVoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
class LikedVoucherApiControllerTest {

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
    LikedVoucherRepository likedVoucherRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void saveLikedVoucher() throws Exception {
        // given
        Voucher voucher = Voucher.builder()
                .title("voucher")
                .price(4_000L)
                .image("voucher.png")
                .build();

        Member member = Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);
        Member memberSaved = memberRepository.save(member);

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);
        String url = "http://localhost:" + port + "/api/liked-vouchers";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(voucherSaved.getId())));

        // then
        response.andExpect(status().isCreated());

        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();
        LikedVoucher found = likedVoucherList.get(0);

        assertThat(found.getVoucher()).isEqualTo(voucherSaved);
        assertThat(found.getMember()).isEqualTo(memberSaved);
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        Member member = Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Member memberSaved = memberRepository.save(member);

        for (int i = 1; i <= 10; i++) {
            Voucher voucher = Voucher.builder()
                    .title("voucher" + i)
                    .price(4_000L)
                    .image("voucher" + i + ".png")
                    .build();

            Voucher voucherSaved = voucherRepository.save(voucher);
            memberSaved.addLikedVoucher(new LikedVoucher(voucherSaved));
        }

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);
        String url = "http://localhost:" + port + "/api/liked-vouchers";

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void deleteLikedVoucher() throws Exception {
        // given
        Voucher voucher = Voucher.builder()
                .title("voucher")
                .price(4_000L)
                .image("voucher.png")
                .build();

        Member member = Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);
        Member memberSaved = memberRepository.save(member);
        LikedVoucher likedVoucherSaved = likedVoucherRepository.save(LikedVoucher.builder().voucher(voucherSaved).build());
        likedVoucherSaved.setMember(member);

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);
        String url = "http://localhost:" + port + "/api/liked-vouchers/" + voucherSaved.getId();

        // when
        ResultActions response = mockMvc.perform(delete(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isOk());
        assertThat(likedVoucherRepository.existsById(likedVoucherSaved.getId())).isFalse();
    }
}