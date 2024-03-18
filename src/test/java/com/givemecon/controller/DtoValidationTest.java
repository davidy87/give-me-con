package com.givemecon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtUtils;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.givemecon.config.auth.enums.JwtAuthHeader.*;
import static com.givemecon.config.auth.enums.Role.*;
import static com.givemecon.controller.TokenHeaderUtils.*;
import static com.givemecon.domain.member.MemberDto.*;
import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherDto.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
@SpringBootTest
public class DtoValidationTest {

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
                .username("tester")
                .email("test@gmail.com")
                .role(ADMIN)
                .build());

        tokenInfo = jwtUtils.getTokenInfo(new TokenRequest(member));
    }

    @Test
    @DisplayName("Category Request DTO 검증 실패 테스트")
    void categoryDtoFailed() throws Exception {
        // given
        MockPart name = new MockPart("name", null);
        MockMultipartFile iconFile = new MockMultipartFile("iconFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/categories")
                .file(iconFile)
                .part(name)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
        );

        // then
        log.info(saveResult.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        saveResult
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(NOT_VALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(NOT_VALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(NOT_VALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("Brand Request DTO 검증 실패 테스트")
    void brandDtoFailed() throws Exception {
        // given
        MockPart invalidCategoryId = new MockPart("categoryId", "aaa".getBytes());
        MockPart name = new MockPart("name", null);
        MockMultipartFile iconFile = new MockMultipartFile("iconFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/brands")
                .file(iconFile)
                .part(invalidCategoryId)
                .part(name)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
        );

        // then
        log.info(saveResult.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        saveResult
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(NOT_VALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(NOT_VALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(NOT_VALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("Voucher Request DTO 검증 실패 테스트")
    void voucherDtoFailed() throws Exception {
        // given
        MockPart price = new MockPart("price", null);
        MockPart title = new MockPart("title", null);
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/vouchers")
                .file(imageFile)
                .part(price)
                .part(title)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
        );

        // then
        log.info(saveResult.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        saveResult
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(NOT_VALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(NOT_VALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(NOT_VALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("VoucherForSale Request DTO 검증 실패 테스트")
    void voucherForSaleDtoFailed() throws Exception {
        // given
        MockPart title = new MockPart("title", null);
        MockPart price = new MockPart("price", null);
        MockPart expDate = new MockPart("expDate", null);
        MockPart barcode = new MockPart("barcode", null);
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/vouchers-for-sale")
                .file(imageFile)
                .part(title)
                .part(price)
                .part(expDate)
                .part(barcode)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
        );

        // then
        log.info(saveResult.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        saveResult
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(NOT_VALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(NOT_VALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(NOT_VALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("PurchasedVoucher Request DTO 검증 실패 테스트")
    void purchasedVoucherDtoFailed() throws Exception {
        // given
        PurchasedVoucherRequest requestDto = new PurchasedVoucherRequest(0L);
        PurchasedVoucherRequestList requestDtoList = new PurchasedVoucherRequestList(List.of(requestDto));

        // when
        ResultActions response = mockMvc.perform(post("/api/purchased-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDtoList))
        );

        log.info(response.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(NOT_VALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(NOT_VALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(NOT_VALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }
}
