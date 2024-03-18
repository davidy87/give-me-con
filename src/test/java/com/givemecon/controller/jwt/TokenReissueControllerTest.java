package com.givemecon.controller.jwt;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtUtils;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.givemecon.config.auth.enums.JwtAuthHeader.*;
import static com.givemecon.config.auth.enums.Role.*;
import static com.givemecon.controller.TokenHeaderUtils.*;
import static com.givemecon.domain.member.MemberDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TokenReissueControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    MemberRepository memberRepository;

    Member member;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        member = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(USER)
                .build());

        tokenInfo = jwtUtils.getTokenInfo(new TokenRequest(member));
    }

    @Test
    void reissueAccessToken() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/auth/refresh";
        Claims oldClaims = jwtUtils.getClaims(tokenInfo.getAccessToken());

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .header(REFRESH_TOKEN.getName(), getRefreshTokenHeader(tokenInfo)));

        // then
        String newAccessToken = response.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Claims newClaims = jwtUtils.getClaims(newAccessToken);
        assertThat(newClaims.getSubject()).isEqualTo(oldClaims.getSubject());
    }
}