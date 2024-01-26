package com.givemecon.web;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.givemecon.util.error.ErrorCode.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
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
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MemberRepository memberRepository;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Member seller = memberRepository.save(Member.builder()
                .username("tester")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build());

        tokenInfo = jwtTokenProvider.getTokenInfo(seller);
    }

    @Test
    @DisplayName("Category Request DTO 검증 실패 테스트")
    void categoryDtoFailed() throws Exception {
        // given
        MockPart name = new MockPart("name", null);
        MockMultipartFile iconFile = new MockMultipartFile("icon", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/categories")
                .file(iconFile)
                .part(name)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
        );

        // then
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
        MockPart nullCategoryId = new MockPart("categoryId", null);
        MockPart inValidCategoryId = new MockPart("categoryId", Long.valueOf(0).toString().getBytes());
        MockPart name = new MockPart("name", null);
        MockMultipartFile iconFile = new MockMultipartFile("icon", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/brands")
                .file(iconFile)
                .part(nullCategoryId)
                .part(name)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
        );

        ResultActions updateResult = mockMvc.perform(multipart("/api/brands/1")
                .part(inValidCategoryId)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
        );

        // then
        saveResult
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(NOT_VALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(NOT_VALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(NOT_VALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());

        updateResult
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
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
        );

        // then
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
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
        );

        // then
        saveResult
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(NOT_VALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(NOT_VALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(NOT_VALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }
}
