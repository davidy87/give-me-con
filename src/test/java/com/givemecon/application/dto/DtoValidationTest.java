package com.givemecon.application.dto;

import com.givemecon.controller.ControllerTestEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.givemecon.application.dto.OrderDto.OrderRequest;
import static com.givemecon.application.dto.PaymentDto.PaymentRequest;
import static com.givemecon.application.dto.PurchasedVoucherDto.PurchasedVoucherRequest;
import static com.givemecon.application.dto.PurchasedVoucherDto.PurchasedVoucherRequestList;
import static com.givemecon.application.dto.VoucherDto.StatusUpdateRequest;
import static com.givemecon.domain.entity.voucher.VoucherStatus.SALE_REJECTED;
import static com.givemecon.common.exception.controlleradvice.ValidationErrorCode.INVALID_ARGUMENT;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class DtoValidationTest extends ControllerTestEnvironment {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Category Request DTO 검증 테스트")
    void categoryRequestWithInvalidData() throws Exception {
        // given
        MockPart name = new MockPart("name", null);
        MockMultipartFile iconFile = new MockMultipartFile("iconFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/admin/categories")
                .file(iconFile)
                .part(name));

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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Brand Request DTO 검증 테스트")
    void brandRequestWithInvalidData() throws Exception {
        // given
        MockPart invalidCategoryId = new MockPart("categoryId", "aaa".getBytes());
        MockPart name = new MockPart("name", null);
        MockMultipartFile iconFile = new MockMultipartFile("iconFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/admin/brands")
                .file(iconFile)
                .part(invalidCategoryId)
                .part(name));

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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("VoucherKind Request DTO 검증 테스트")
    void voucherKindRequestWithInvalidData() throws Exception {
        // given
        MockPart price = new MockPart("price", null);
        MockPart title = new MockPart("title", null);
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", (byte[]) null);

        // when
        ResultActions saveResult = mockMvc.perform(multipart("/api/admin/voucher-kinds")
                .file(imageFile)
                .part(price)
                .part(title));

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
    @WithMockUser(roles = "USER")
    @DisplayName("Voucher Request DTO 검증 테스트 1 - Voucher 저장 요청 시, 모든 데이터는 null이서서는 안된다.")
    void voucherRequestWithInvalidData() throws Exception {
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
                .part(barcode));

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
    @WithMockUser(roles = "USER")
    @DisplayName(
            "Voucher Request DTO 검증 테스트 2 " +
                    "- 상태별 Voucher 조회 시, 파라미터로 보내는 statusCode는 최소 0, 최대 5까지만 가능하다."
    )
    void voucherRequestWithInvalidStatusCode() throws Exception {
        // given
        Integer invalidStatusCode = 6;

        // when
        ResultActions saveResult = mockMvc.perform(get("/api/vouchers")
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName(
            "Voucher Request DTO 검증 테스트 3 " +
                    "- 상태 수정 요청 DTO의 statusCode가 1(SALE_REJECTED)일 경우, rejectedReason이 무조건 같이 전달되어야 한다."
    )
    void voucherRequestWithInvalidSaleRejection() throws Exception {
        // given
        Integer statusCode = SALE_REJECTED.ordinal();

        // when
        StatusUpdateRequest requestBody = new StatusUpdateRequest();
        requestBody.setStatusCode(statusCode);

        ResultActions saveResult = mockMvc.perform(put("/api/admin/vouchers/{id}", 1)
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
    @WithMockUser(roles = "USER")
    @DisplayName("PurchasedVoucher Request DTO 검증 테스트 1 - voucherId는 1 이상이어야 한다.")
    void purchasedVoucherRequestWithInvalidVoucherId() throws Exception {
        // given
        PurchasedVoucherRequest requestDto = new PurchasedVoucherRequest(0L);
        PurchasedVoucherRequestList requestDtoList = new PurchasedVoucherRequestList(List.of(requestDto));

        // when
        ResultActions response = mockMvc.perform(post("/api/purchased-vouchers")
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
    @DisplayName("PurchasedVoucher Request DTO 검증 테스트 2 - PurchasedVoucherRequestList는 비어있으면 안된다.")
    void purchasedVoucherRequestWithEmptyList() throws Exception {
        // given
        PurchasedVoucherRequestList requestDtoList = new PurchasedVoucherRequestList(List.of());

        // when
        ResultActions response = mockMvc.perform(post("/api/purchased-vouchers")
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
    @DisplayName("OrderRequest DTO 검증 테스트 1 - voucherIdList는 비어있으면 안된다.")
    void orderRequestWithEmptyList() throws Exception {
        // given
        List<Long> voucherIdList = List.of();
        OrderRequest orderRequest = new OrderRequest(voucherIdList);

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
    @DisplayName("OrderRequest DTO 검증 테스트 2 - voucherId는 모두 1 이상이어야 한다.")
    void orderRequestWithInvalidVoucherId() throws Exception {
        // given
        List<Long> voucherIdList = List.of(0L, 1L, 2L);
        OrderRequest orderRequest = new OrderRequest(voucherIdList);

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
