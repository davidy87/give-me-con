package com.givemecon.controller.api;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.controller.TokenHeaderUtils.getAccessTokenHeader;
import static com.givemecon.domain.member.MemberDto.*;
import static com.givemecon.util.error.ErrorCode.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApiExceptionControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    MemberRepository memberRepository;

    Member member;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        member = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .authority(ADMIN)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
    }

    @Test
    void categoryExceptionTest() throws Exception {
        // given
        String newName = "newCategory";
        MockMultipartFile newIconFile = new MockMultipartFile(
                "icon",
                "newCategory.jpg",
                "image/jpg",
                "newCategory.jpg".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/categories/{id}", 1)
                .file(newIconFile)
                .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8)))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(ENTITY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("error.code").value(ENTITY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("error.message").value(ENTITY_NOT_FOUND.getMessage()));
    }

    @Test
    void brandsExceptionTest() throws Exception {
        // given
        String newName = "newCategory";
        MockMultipartFile newIconFile = new MockMultipartFile(
                "icon",
                "new_brand.jpg",
                "image/jpg",
                "new_brand.jpg".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/brands/{id}", 1)
                .file(newIconFile)
                .part(new MockPart("categoryId", String.valueOf(1).getBytes(StandardCharsets.UTF_8)))
                .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8)))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(ENTITY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("error.code").value(ENTITY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("error.message").value(ENTITY_NOT_FOUND.getMessage()));
    }

    @Test
    void voucherExceptionTest() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/vouchers/" + 1;

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(ENTITY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("error.code").value(ENTITY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("error.message").value(ENTITY_NOT_FOUND.getMessage()));
    }

    @Test
    void memberExceptionTest() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/members/" + 1;

        // when
        ResultActions response = mockMvc.perform(delete(url)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(ENTITY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("error.code").value(ENTITY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("error.message").value(ENTITY_NOT_FOUND.getMessage()));
    }

    @Test
    void invalidJwtException() throws Exception {
        // given
        String invalidAccessToken = tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken() + "a";
        String url = "http://localhost:" + port + "/api/liked-vouchers/" + 1;

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header(AUTHORIZATION.getName(), invalidAccessToken));

        // then
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.code").value(TOKEN_NOT_AUTHENTICATED.getCode()))
                .andExpect(jsonPath("error.status").value(TOKEN_NOT_AUTHENTICATED.getStatus()))
                .andExpect(jsonPath("error.message").value(TOKEN_NOT_AUTHENTICATED.getMessage()));
    }
}
