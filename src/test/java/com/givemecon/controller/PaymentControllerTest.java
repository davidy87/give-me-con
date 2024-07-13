package com.givemecon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.enums.Authority;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.image.voucherkind.VoucherKindImage;
import com.givemecon.domain.image.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.order.Order;
import com.givemecon.domain.order.OrderRepository;
import com.givemecon.domain.payment.PaymentRepository;
import com.givemecon.domain.payment.toss.PaymentConfirmation;
import com.givemecon.domain.payment.toss.TossPaymentsRestClient;
import com.givemecon.domain.voucherkind.VoucherKind;
import com.givemecon.domain.voucherkind.VoucherKindRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.voucher.VoucherStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Map;

import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.domain.payment.PaymentDto.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
class PaymentControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Autowired
    VoucherKindImageRepository voucherKindImageRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MemberRepository memberRepository;

    @MockBean
    TossPaymentsRestClient tossPaymentsRestClient;

    @Autowired
    ObjectMapper objectMapper;

    Member buyer;

    Order order;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

        buyer = memberRepository.save(Member.builder()
                .username("tester")
                .email("test@gmail.com")
                .authority(Authority.USER)
                .build());

        order = orderRepository.save(new Order("ORDER-NUMBER", buyer));

        Brand brand = brandRepository.save(Brand.builder()
                .name("Brand")
                .build());

        VoucherKind voucherKind = VoucherKind.builder()
                .title("voucherKind")
                .description("description")
                .caution("caution")
                .build();

        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        voucherKind.updateBrand(brand);
        voucherKind.updateVoucherKindImage(voucherKindImage);
        voucherKindRepository.save(voucherKind);

        Voucher voucher =
                voucherRepository.save(Voucher.builder()
                        .price(4_000L)
                        .barcode("1111 1111 1111")
                        .expDate(LocalDate.now())
                        .build());

        voucher.updateVoucherKind(voucherKind);
        voucher.updateOrder(order);
        voucher.updateStatus(VoucherStatus.ORDER_PLACED);
        order.updateQuantity(1);
        order.updateAmount(voucher.getPrice());
    }

    @Test
    @WithMockUser(roles = "USER", username = "tester")
    @DisplayName("결제 승인 요청 API 테스트")
    void confirmPayment() throws Exception {
        // given
        String paymentKey = "PAYMENT-KEY";
        String orderId = order.getOrderNumber();
        String orderName = "orderName";
        Long amount = 4_000L;
        Map<String, String> receipt =  Map.of("url", "receiptUrl");

        PaymentRequest paymentRequest = new PaymentRequest(paymentKey, orderId, amount);
        PaymentConfirmation paymentConfirmation =
                new PaymentConfirmation(paymentKey, "DONE", orderId, orderName, amount, receipt);

        Mockito.when(tossPaymentsRestClient.requestPaymentConfirmation(any(PaymentRequest.class)))
                .thenReturn(paymentConfirmation);

        // when
        String requestBody = objectMapper.writeValueAsString(paymentRequest);
        ResultActions response = mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("amount").value(amount))
                .andExpect(jsonPath("orderId").value(orderId))
                .andExpect(jsonPath("orderName").value(orderName))
                .andExpect(jsonPath("receiptUrl").value(receipt.get("url")))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("토스페이먼츠에서 제공하는 결제의 키 값"),
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
}