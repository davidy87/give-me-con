package com.givemecon.controller.service;

import com.givemecon.application.dto.MemberDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.ControllerTestEnvironment;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.order.Order;
import com.givemecon.domain.entity.payment.OrderInfo;
import com.givemecon.domain.entity.payment.Payment;
import com.givemecon.domain.entity.payment.PaymentMethod;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherStatus;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.infrastructure.tosspayments.PaymentConfirmation;
import com.givemecon.infrastructure.tosspayments.TossPaymentsRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Map;

import static com.givemecon.application.dto.PaymentDto.PaymentRequest;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.domain.entity.member.Role.*;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest extends ControllerTestEnvironment {

    @Autowired
    TossPaymentsRestClient tossPaymentsRestClient;  // MockBean

    TokenInfo tokenInfo;

    Order order;

    @BeforeEach
    void setup() {
        Member buyer = memberRepository.save(Member.builder()
                .username("buyer")
                .email("buyer@gmail.com")
                .role(USER)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new MemberDto.TokenRequest(buyer));

        order = orderRepository.save(new Order("ORDER-NUMBER", buyer));

        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("categoryIcon")
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build());

        BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("brandIcon")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("Brand")
                .brandIcon(brandIcon)
                .category(category)
                .build());

        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        VoucherKind voucherKind = VoucherKind.builder()
                .title("voucherKind")
                .description("description")
                .caution("caution")
                .voucherKindImage(voucherKindImage)
                .brand(brand)
                .build();

        voucherKindRepository.save(voucherKind);

        Voucher voucher =
                voucherRepository.save(Voucher.builder()
                        .price(4_000L)
                        .barcode("1111 1111 1111")
                        .expDate(LocalDate.now())
                        .voucherKind(voucherKind)
                        .build());

        voucher.updateOrder(order);
        voucher.updateStatus(VoucherStatus.ORDER_PLACED);
        order.updateQuantity(1);
        order.updateAmount(voucher.getPrice());
    }

    @Test
    @DisplayName("결제 승인 요청 API 테스트")
    void confirmPayment() throws Exception {
        // given
        String paymentKey = "PAYMENT-KEY";
        String orderNumber = order.getOrderNumber();
        String orderName = "orderName";
        Long amount = 4_000L;
        Map<String, String> receipt =  Map.of("url", "receiptUrl");

        PaymentRequest paymentRequest = new PaymentRequest(paymentKey, orderNumber, amount);
        PaymentConfirmation paymentConfirmation =
                new PaymentConfirmation(paymentKey, "DONE", orderNumber, orderName, amount, receipt);

        Mockito.when(tossPaymentsRestClient.requestPaymentConfirmation(any(PaymentRequest.class)))
                .thenReturn(paymentConfirmation);

        // when
        String requestBody = objectMapper.writeValueAsString(paymentRequest);
        ResultActions response = mockMvc.perform(post("/api/payments/confirm")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("amount").value(amount))
                .andExpect(jsonPath("orderId").value(orderNumber))
                .andExpect(jsonPath("orderName").value(orderName))
                .andExpect(jsonPath("receiptUrl").value(receipt.get("url")))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("토스페이먼츠에서 제공하는 결제 키"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문번호"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("주문금액")
                        ),
                        responseFields(
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("주문금액"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문번호"),
                                fieldWithPath("orderName").type(JsonFieldType.STRING).description("구매상품"),
                                fieldWithPath("receiptUrl").type(JsonFieldType.STRING).description("발행된 영수증")
                        ))
                );
    }

    @Test
    @DisplayName("결제 내역 조회 요청 API 테스트")
    void findPaymentHistory() throws Exception {
        // given
        OrderInfo orderInfo = new OrderInfo(order.getOrderNumber(), "Americano T", 4_000L);
        Payment payment = paymentRepository.save(Payment.builder()
                .paymentKey("PAYMENT-KEY")
                .method(PaymentMethod.CARD)
                .receiptUrl("receiptUrl")
                .orderInfo(orderInfo)
                .build());

        // when
        ResultActions response = mockMvc.perform(get("/api/payments/{paymentKey}", payment.getPaymentKey())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("amount").value(orderInfo.getAmount()))
                .andExpect(jsonPath("orderId").value(orderInfo.getOrderNumber()))
                .andExpect(jsonPath("orderName").value(orderInfo.getOrderName()))
                .andExpect(jsonPath("receiptUrl").value(payment.getReceiptUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("paymentKey").description("결제 키")
                        ),
                        responseFields(
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("주문금액"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문번호"),
                                fieldWithPath("orderName").type(JsonFieldType.STRING).description("구매상품"),
                                fieldWithPath("receiptUrl").type(JsonFieldType.STRING).description("발행된 영수증")
                        ))
                );
    }
}