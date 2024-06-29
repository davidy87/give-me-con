package com.givemecon.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.enums.Authority;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.image.voucher.VoucherImageRepository;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImageRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.order.Order;
import com.givemecon.domain.order.OrderRepository;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.givemecon.controller.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.controller.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.domain.order.OrderDto.*;
import static com.givemecon.domain.order.OrderStatus.CONFIRMED;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus.USABLE;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
class OrderControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherImageRepository voucherImageRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    VoucherForSaleImageRepository voucherForSaleImageRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ObjectMapper objectMapper;

    Member buyer;

    List<Long> voucherForSaleIdList;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

        buyer = memberRepository.save(Member.builder()
                .email("buyer@gmail.com")
                .username("buyer")
                .authority(Authority.USER)
                .build());

        Member seller = memberRepository.save(Member.builder()
                .email("seller@gmail.com")
                .username("seller")
                .authority(Authority.USER)
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("Brand")
                .build());

        Voucher voucher = Voucher.builder()
                .title("voucher")
                .description("description")
                .caution("caution")
                .build();

        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        voucher.updateBrand(brand);
        voucher.updateVoucherImage(voucherImage);
        voucherRepository.save(voucher);

        voucherForSaleIdList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            VoucherForSale voucherForSale =
                    voucherForSaleRepository.save(VoucherForSale.builder()
                            .price(4_000L)
                            .barcode("1111 1111 1111")
                            .expDate(LocalDate.now())
                            .build());

            VoucherForSaleImage voucherForSaleImage =
                    voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                            .imageKey("imageKey" + i)
                            .imageUrl("test_image" + i + ".png")
                            .originalName("test_image")
                            .build());

            voucherForSale.updateStatus(FOR_SALE);
            voucherForSale.updateSeller(seller);
            voucherForSale.updateVoucher(voucher);
            voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
            voucherForSaleIdList.add(voucherForSale.getId());
        }
    }

    @Test
    @WithMockUser(roles = "USER", username = "buyer")
    @DisplayName("주문 생성 요청 API 테스트")
    void placeOrder() throws Exception {
        // given
        OrderRequest orderRequest = new OrderRequest(voucherForSaleIdList);

        // when
        String requestBody = new ObjectMapper().writeValueAsString(orderRequest);

        ResultActions response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        String responseBody = response.andReturn().getResponse().getContentAsString();
        OrderNumberResponse orderNumberResponse = objectMapper.readValue(responseBody, OrderNumberResponse.class);

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("orderNumber").value(orderNumberResponse.getOrderNumber()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("voucherForSaleIdList").type(JsonFieldType.ARRAY).description("주문할 기프티콘 id 리스트")
                        ),
                        responseFields(
                                fieldWithPath("orderNumber").type(JsonFieldType.STRING).description("주문 번호")
                        ))
                );
    }

    @Test
    @WithMockUser(roles = "USER", username = "buyer")
    @DisplayName("주문 조회 요청 API 테스트")
    void findOrder() throws Exception {
        // given
        String orderNumber = UUID.randomUUID().toString();
        Order order = orderRepository.save(new Order(orderNumber, buyer));

        voucherForSaleRepository.findAll()
                .forEach(voucherForSale -> voucherForSale.updateOrder(order));

        // when
        ResultActions response = mockMvc.perform(get("/api/orders/{orderNumber}", order.getOrderNumber()));

        // then
        String responseBody = response.andReturn().getResponse().getContentAsString();
        OrderSummary orderSummary = objectMapper.readValue(responseBody, OrderSummary.class);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("quantity").value(orderSummary.getQuantity()))
                .andExpect(jsonPath("totalPrice").value(orderSummary.getTotalPrice()))
                .andExpect(jsonPath("orderItems").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("orderNumber").description("주문번호")
                        ),
                        responseFields(
                                fieldWithPath("orderNumber").type(JsonFieldType.STRING).description("주문번호"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("주문 체결 상태"),
                                fieldWithPath("customerName").type(JsonFieldType.STRING).description("구매자 이름"),
                                fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("주문 수량"),
                                fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("총 주문 가격"),
                                fieldWithPath("orderItems").type(JsonFieldType.ARRAY).description("주문 품목 리스트"),
                                fieldWithPath("orderItems.[].voucherForSaleId").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("orderItems.[].price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("orderItems.[].brandName").type(JsonFieldType.STRING).description("기프티콘 브랜드 이름"),
                                fieldWithPath("orderItems.[].title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("orderItems.[].voucherImageUrl").type(JsonFieldType.STRING).description("기프티콘 종류 이미지"),
                                fieldWithPath("orderItems.[].expDate").type(JsonFieldType.STRING).description("기프티콘 유효기간"),
                                fieldWithPath("orderItems.[].status").type(JsonFieldType.STRING).description("기프티콘 상태")
                        ))
                );
    }

    @Test
    @WithMockUser(roles = "USER", username = "buyer")
    @DisplayName("주문 체결 요청 API 테스트")
    void confirmOrder() throws Exception {
        // given
        String orderNumber = UUID.randomUUID().toString();
        Order order = orderRepository.save(new Order(orderNumber, buyer));

        voucherForSaleRepository.findAll()
                .forEach(voucherForSale -> voucherForSale.updateOrder(order));

        // when
        ResultActions response = mockMvc.perform(put("/api/orders/{orderNumber}", order.getOrderNumber()));

        // then
        Optional<Order> orderFound = orderRepository.findById(order.getId());
        List<PurchasedVoucher> purchasedVouchers = purchasedVoucherRepository.findAll();

        assertThat(orderFound).isPresent();
        assertThat(orderFound.get().getStatus()).isSameAs(CONFIRMED);
        assertThat(purchasedVouchers.size()).isEqualTo(voucherForSaleIdList.size());

        purchasedVouchers.forEach(purchasedVoucher -> {
            assertThat(purchasedVoucher.getStatus()).isSameAs(USABLE);
            assertThat(purchasedVoucher.getOwner()).isSameAs(buyer);
        });

        String responseBody = response.andReturn().getResponse().getContentAsString();
        OrderNumberResponse orderNumberResponse = objectMapper.readValue(responseBody, OrderNumberResponse.class);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("orderNumber").value(orderNumberResponse.getOrderNumber()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("orderNumber").description("체결할 주문의 주문번호")
                        ),
                        responseFields(
                                fieldWithPath("orderNumber").type(JsonFieldType.STRING).description("체결 완료된 주문의 주문번호")
                        ))
                );
    }

    @Test
    @WithMockUser(roles = "USER", username = "buyer")
    @DisplayName("주문 취소 요청 API 테스트")
    void cancelOrder() throws Exception {
        // given
        String orderNumber = UUID.randomUUID().toString();
        Order order = orderRepository.save(new Order(orderNumber, buyer));

        voucherForSaleRepository.findAll()
                .forEach(voucherForSale -> voucherForSale.updateOrder(order));

        // when
        ResultActions response = mockMvc.perform(delete("/api/orders/{orderNumber}", order.getOrderNumber()));

        // then
        Optional<Order> orderFound = orderRepository.findById(order.getId());
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAllByOrder(order);
        assertThat(orderFound).isNotPresent();
        assertThat(voucherForSaleList).isEmpty();

        String responseBody = response.andReturn().getResponse().getContentAsString();
        OrderNumberResponse orderNumberResponse = objectMapper.readValue(responseBody, OrderNumberResponse.class);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("orderNumber").value(orderNumberResponse.getOrderNumber()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("orderNumber").description("취소할 주문의 주문번호")
                        ),
                        responseFields(
                                fieldWithPath("orderNumber").type(JsonFieldType.STRING).description("취소 처리된 주문의 주문번호")
                        ))
                );
    }
}