package com.givemecon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.controller.TokenHeaderUtils.*;
import static com.givemecon.domain.member.MemberDto.*;
import static com.givemecon.domain.order.OrderDto.*;
import static com.givemecon.domain.payment.PaymentDto.*;
import static com.givemecon.domain.voucher.VoucherDto.*;
import static com.givemecon.domain.voucher.VoucherStatus.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherDto.*;
import static com.givemecon.util.error.GlobalErrorCode.INVALID_ARGUMENT;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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
    JwtTokenService jwtTokenService;

    @Autowired
    MemberRepository memberRepository;

    Member member;

    TokenInfo tokenInfo;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        member = memberRepository.save(Member.builder()
                .username("tester")
                .email("test@gmail.com")
                .authority(ADMIN)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
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
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
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
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("VoucherKind Request DTO 검증 실패 테스트")
    void voucherDtoFailed() throws Exception {
        // given
        MockPart price = new MockPart("price", null);
        MockPart title = new MockPart("title", null);
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/voucher-kinds")
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
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("Voucher Request DTO 검증 실패 테스트")
    void voucherForSaleDtoFailed() throws Exception {
        // given
        MockPart title = new MockPart("title", null);
        MockPart price = new MockPart("price", null);
        MockPart expDate = new MockPart("expDate", null);
        MockPart barcode = new MockPart("barcode", null);
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/vouchers")
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
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("상태별 Voucher 조회 시, 파라미터로 보내는 statusCode는 최소 0, 최대 4까지만 가능하다.")
    void statusCodeParameterFailed() throws Exception {
        // given
        Integer invalidStatusCode = 5;

        // when
        ResultActions saveResult = mockMvc.perform(get("/api/vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .queryParam("statusCode", String.valueOf(invalidStatusCode)));

        // then
        log.info(saveResult.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        saveResult
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("상태 수정 요청 DTO의 statusCode가 3(REJECTED)일 경우, rejectedReason이 무조건 같이 전달되어야 한다.")
    void saleRejectionRequestFailed() throws Exception {
        // given
        Integer statusCode = REJECTED.ordinal();

        // when
        StatusUpdateRequest requestBody = new StatusUpdateRequest();
        requestBody.setStatusCode(statusCode);

        ResultActions saveResult = mockMvc.perform(put("/api/vouchers/{id}", 1)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));

        // then
        log.info(saveResult.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        saveResult
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("PurchasedVoucher Request DTO 검증 테스트 1")
    void invalidPurchasedVoucherRequest() throws Exception {
        // given
        PurchasedVoucherRequest requestDto = new PurchasedVoucherRequest(0L);
        PurchasedVoucherRequestList requestDtoList = new PurchasedVoucherRequestList(List.of(requestDto));

        // when
        ResultActions response = mockMvc.perform(post("/api/purchased-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDtoList))
        );

        log.info(response.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @DisplayName("PurchasedVoucher Request DTO 검증 테스트 2")
    void invalidPurchasedVoucherRequestList() throws Exception {
        // given
        PurchasedVoucherRequestList requestDtoList = new PurchasedVoucherRequestList(List.of());

        // when
        ResultActions response = mockMvc.perform(post("/api/purchased-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDtoList))
        );

        log.info(response.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("OrderRequest DTO 검증 테스트 1 - voucherForSaleIdList는 비어있으면 안된다.")
    void orderRequestWithEmptyList() throws Exception {
        // given
        OrderRequest orderRequest = new OrderRequest(List.of());

        // when
        ResultActions response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)));

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("OrderRequest DTO 검증 테스트 2 - voucherForSaleId는 모두 1 이상이어야 한다.")
    void orderRequestWithInvalidVoucherForSaleId() throws Exception {
        // given
        OrderRequest orderRequest = new OrderRequest(List.of(0L, 1L, 2L));

        // when
        ResultActions response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)));

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PaymentRequest DTO 검증 테스트 1 - paymentKey는 null이거나 빈 문자열이어서는 안된다.")
    void paymentRequestWithInvalidPaymentKey() throws Exception {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("", "ORDER-ID", 4_000L);

        // when
        ResultActions response = mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)));

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PaymentRequest DTO 검증 테스트 2 - orderId는 null이거나 빈 문자열이어서는 안된다.")
    void paymentRequestWithInvalidOrderId() throws Exception {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("PAYMENT-KEY", null, 4_000L);

        // when
        ResultActions response = mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)));

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PaymentRequest DTO 검증 테스트 3 - amount는 null이서선 안되며, 최소 0이어야 한다.")
    void paymentRequestWithInvalidAmount() throws Exception {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("PAYMENT-KEY", "ORDER-ID", null);

        // when
        ResultActions response = mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)));

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("error.status").value(INVALID_ARGUMENT.getStatus()))
                .andExpect(jsonPath("error.code").value(INVALID_ARGUMENT.getCode()))
                .andExpect(jsonPath("error.message").value(INVALID_ARGUMENT.getMessage()))
                .andExpect(jsonPath("error.fieldErrors").isNotEmpty());
    }
}
