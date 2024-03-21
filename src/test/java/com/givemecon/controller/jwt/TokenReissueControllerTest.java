package com.givemecon.controller.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtUtils;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
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
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
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
        String url = "http://localhost:" + port + "/api/auth/reissue";
        Claims oldClaims = jwtUtils.getClaims(tokenInfo.getAccessToken());

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .header(REFRESH_TOKEN.getName(), getRefreshTokenHeader(tokenInfo)));

        // then
        String responseString = response.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenInfo newTokenInfo = new ObjectMapper().readValue(responseString, TokenInfo.class);
        Claims newClaims = jwtUtils.getClaims(newTokenInfo.getAccessToken());
        assertThat(newClaims.get("username")).isEqualTo(oldClaims.get("username"));
    }
}