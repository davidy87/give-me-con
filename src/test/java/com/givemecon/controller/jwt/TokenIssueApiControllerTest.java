package com.givemecon.controller.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.controller.ApiDocumentUtils.*;
import static com.givemecon.controller.TokenHeaderUtils.*;
import static com.givemecon.domain.member.MemberDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
public class TokenIssueApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();
    }

    @Test
    void reissueToken() throws Exception {
        // given
        Member member = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .authority(USER)
                .build());

        TokenInfo tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
        Claims oldClaims = jwtTokenService.getClaims(tokenInfo.getAccessToken());

        // when
        ResultActions response = mockMvc.perform(get("/api/auth/reissue")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .header(REFRESH_TOKEN.getName(), getRefreshTokenHeader(tokenInfo)));

        // then
        String responseString = response.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenInfo newTokenInfo = new ObjectMapper().readValue(responseString, TokenInfo.class);
        Claims newClaims = jwtTokenService.getClaims(newTokenInfo.getAccessToken());
        assertThat(newClaims.get("username")).isEqualTo(oldClaims.get("username"));

        // API Documentation
        response.andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithRefreshToken(),
                        getDocumentResponse(),
                        responseFields(
                            fieldWithPath("grantType").type(JsonFieldType.STRING).description("Token의 grant type"),
                            fieldWithPath("accessToken").type(JsonFieldType.STRING).description("Access Token"),
                            fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("Refresh Token"),
                            fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 닉네임"),
                            fieldWithPath("authority").type(JsonFieldType.STRING).description("권한")
                        ))
        );
    }
}