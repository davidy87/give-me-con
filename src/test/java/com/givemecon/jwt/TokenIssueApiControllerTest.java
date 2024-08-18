package com.givemecon.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static com.givemecon.common.auth.enums.JwtAuthHeader.*;
import static com.givemecon.common.error.GlobalErrorCode.MISSING_REQUEST_PARAMETER;
import static com.givemecon.common.error.GlobalErrorCode.TOKEN_NOT_AUTHENTICATED;
import static com.givemecon.domain.entity.member.Role.*;
import static com.givemecon.common.auth.enums.OAuth2ParameterName.*;
import static com.givemecon.util.ApiDocumentUtils.*;
import static com.givemecon.util.TokenHeaderUtils.*;
import static com.givemecon.application.dto.MemberDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    RedisTemplate<String, TokenInfo> redisTemplate;

    Member member;

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
    }

    @Test
    void issueToken() throws Exception {
        // given
        String authorizationCode = UUID.randomUUID().toString();
        TokenInfo tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
        Claims claims = jwtTokenService.getClaims(tokenInfo.getAccessToken());

//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(authorizationCode, tokenInfo);

        redisTemplate.opsForValue().set(authorizationCode, tokenInfo);

        // when
        ResultActions response = mockMvc.perform(get("/api/auth/success")
//                .session(session)
                .queryParam(AUTHORIZATION_CODE.getName(), authorizationCode));

        // then
        String responseString = response.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenInfo newTokenInfo = new ObjectMapper().readValue(responseString, TokenInfo.class);
        Claims foundClaims = jwtTokenService.getClaims(newTokenInfo.getAccessToken());
        assertThat(claims).isEqualTo(foundClaims);
        assertThat(claims.get("username")).isEqualTo(foundClaims.get("username"));

        // API Documentation
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("authorizationCode").description("인가 코드")
                        ),
                        responseFields(
                                fieldWithPath("grantType").type(JsonFieldType.STRING).description("Token의 grant type"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("Access Token"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("Refresh Token"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 닉네임"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("권한")
                        ))
        );
    }

    @Test
    void reissueToken() throws Exception {
        // given
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
        assertThat(oldClaims).isNotEqualTo(newClaims);
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
                    fieldWithPath("role").type(JsonFieldType.STRING).description("권한")
                ))
        );
    }

    @Nested
    @DisplayName("JWT 인증 예외 테스트")
    class ExceptionTest {

        @Test
        void invalidAccessTokenException() throws Exception {
            // given
            String invalidAccessToken = "Bearer " + "INVALID_TOKEN";

            // when
            ResultActions response = mockMvc.perform(get("/api/liked-vouchers/{id}", 1L)
                    .header(AUTHORIZATION.getName(), invalidAccessToken));

            // then
            response.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("error.code").value(TOKEN_NOT_AUTHENTICATED.getCode()))
                    .andExpect(jsonPath("error.status").value(TOKEN_NOT_AUTHENTICATED.getStatus()))
                    .andExpect(jsonPath("error.message").value(TOKEN_NOT_AUTHENTICATED.getMessage()));
        }

        @Test
        @DisplayName("필수 요청 파라미터가 전달되지 않았을 때 요청 처리")
        void missingParameterException() throws Exception {
            ResultActions response = mockMvc.perform(get("/api/auth/success"));

            response.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("error.code").value(MISSING_REQUEST_PARAMETER.getCode()))
                    .andExpect(jsonPath("error.status").value(MISSING_REQUEST_PARAMETER.getStatus()))
                    .andExpect(jsonPath("error.message").value(MISSING_REQUEST_PARAMETER.getMessage()))
                    .andExpect(jsonPath("error.parameterDetails.parameterName").value("authorizationCode"))
                    .andExpect(jsonPath("error.parameterDetails.parameterType").value("String"));
        }
    }
}