package com.givemecon.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.common.exception.concrete.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static com.givemecon.common.auth.enums.JwtAuthHeader.*;
import static com.givemecon.domain.entity.member.Role.*;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static com.givemecon.application.dto.MemberDto.*;
import static com.givemecon.application.dto.VoucherDto.*;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;
import static com.givemecon.common.error.GlobalErrorCode.*;
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
                .role(ADMIN)
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
        ResultActions response = mockMvc.perform(multipart("/api/admin/categories/{id}", 1)
                .file(newIconFile)
                .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8)))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(ENTITY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("error.code").value(ENTITY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("error.message").value(new EntityNotFoundException(Category.class).getMessage()));
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
        ResultActions response = mockMvc.perform(multipart("/api/admin/brands/{id}", 1)
                .file(newIconFile)
                .part(new MockPart("categoryId", String.valueOf(1).getBytes(StandardCharsets.UTF_8)))
                .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8)))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(ENTITY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("error.code").value(ENTITY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("error.message").value(new EntityNotFoundException(Brand.class).getMessage()));
    }

    @Test
    void voucherExceptionTest() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/voucher-kinds/" + 1;

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(ENTITY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("error.code").value(ENTITY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("error.message").value(new EntityNotFoundException(VoucherKind.class).getMessage()));
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
                .andExpect(jsonPath("error.message").value(new EntityNotFoundException(Member.class).getMessage()));
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

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Voucher API 권한 예외 처리 1 - ROLE_USER 권한만 접근 가능한 API에 다른 권한이 접근")
    void voucherForSaleApiRoleException1() throws Exception {
        ResultActions response = mockMvc.perform(get("/api/vouchers-for-sale"));
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Voucher API 권한 예외 처리 2 - ROLE_ADMIN 권한만 접근 가능한 API에 다른 권한이 접근")
    void voucherForSaleApiRoleException2(@Autowired VoucherRepository voucherRepository) throws Exception {
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .build());

        StatusUpdateRequest requestBody = new StatusUpdateRequest();
        requestBody.setStatusCode(FOR_SALE.ordinal());

        ResultActions update = mockMvc.perform(put("/api/vouchers-for-sale/{id}", voucher.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody)));

        ResultActions delete = mockMvc.perform(delete("/api/vouchers-for-sale/{id}", voucher.getId()));

        update.andExpect(status().isForbidden());
        delete.andExpect(status().isForbidden());
    }
}
