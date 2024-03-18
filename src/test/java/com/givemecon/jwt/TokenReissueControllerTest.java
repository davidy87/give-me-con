package com.givemecon.jwt;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.enums.GrantType;
import com.givemecon.config.auth.enums.Role;
import com.givemecon.config.auth.jwt.token.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.givemecon.config.auth.enums.JwtAuthHeader.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
@WithMockUser(authorities = "ROLE_USER", username = "tester")
public class TokenReissueControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void reissueAccessToken() throws Exception {
        // given
        Member member = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(member);
        String url = "http://localhost:" + port + "/api/auth/refresh";
        Claims oldClaims = jwtTokenProvider.getClaims(tokenInfo.getAccessToken());

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header(REFRESH_TOKEN.getName(), GrantType.BEARER.getType() + " " + tokenInfo.getRefreshToken()));

        // then
        String newAccessToken = response.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Claims newClaims = jwtTokenProvider.getClaims(newAccessToken);
        assertThat(newClaims.getSubject()).isEqualTo(oldClaims.getSubject());
    }
}