package com.givemecon.web.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.error.ErrorCode;
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
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@WithMockUser(authorities = "ROLE_USER", username = "tester")
public class JwtExceptionTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void jwtException() throws Exception {
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

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());
        String invalidAccessToken = tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken() + "a";
        String url = "http://localhost:" + port + "/api/liked-vouchers";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .header("Authorization", invalidAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(voucherSaved.getId())));

        // then
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("code").value(ErrorCode.TOKEN_EXPIRED.getCode()))
                .andExpect(jsonPath("status").value(ErrorCode.TOKEN_EXPIRED.getStatus()))
                .andExpect(jsonPath("message").value(ErrorCode.TOKEN_EXPIRED.getMessage()));
    }
}
