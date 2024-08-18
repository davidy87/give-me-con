package com.givemecon.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.application.dto.MemberDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.IntegrationTest;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.order.Order;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.givemecon.application.dto.OrderDto.OrderNumberResponse;
import static com.givemecon.application.dto.OrderDto.OrderRequest;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.domain.entity.member.Role.*;
import static com.givemecon.domain.entity.order.OrderStatus.IN_PROGRESS;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;
import static com.givemecon.domain.entity.voucher.VoucherStatus.ORDER_PLACED;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends IntegrationTest {

    Member buyer;

    TokenInfo buyerTokenInfo;

    List<Long> voucherForSaleIdList;

    @BeforeEach
    void setup() {
        buyer = memberRepository.save(Member.builder()
                .email("buyer@gmail.com")
                .username("buyer")
                .role(USER)
                .build());

        buyerTokenInfo = jwtTokenService.getTokenInfo(new MemberDto.TokenRequest(buyer));

        Member seller = memberRepository.save(Member.builder()
                .email("seller@gmail.com")
                .username("seller")
                .role(USER)
                .build());

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

        voucherForSaleIdList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            VoucherImage voucherImage =
                    voucherImageRepository.save(VoucherImage.builder()
                            .imageKey("imageKey" + i)
                            .imageUrl("test_image" + i + ".png")
                            .originalName("test_image")
                            .build());

            Voucher voucher =
                    voucherRepository.save(Voucher.builder()
                            .price(4_000L)
                            .barcode("1111 1111 1111")
                            .expDate(LocalDate.now())
                            .voucherImage(voucherImage)
                            .voucherKind(voucherKind)
                            .seller(seller)
                            .build());

            voucher.updateStatus(FOR_SALE);
            voucherForSaleIdList.add(voucher.getId());
        }
    }

    @Test
    @DisplayName("주문 생성 요청 API 테스트")
    void placeOrder() throws Exception {
        // given
        OrderRequest orderRequest = new OrderRequest(voucherForSaleIdList);

        // when
        String requestBody = new ObjectMapper().writeValueAsString(orderRequest);

        ResultActions response = mockMvc.perform(post("/api/orders")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(buyerTokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        List<Order> orders = orderRepository.findAll();
        assertThat(orders).isNotEmpty();

        int quantity = 0;
        long amount = 0L;

        for (Voucher voucher : voucherRepository.findAll()) {
            quantity++;
            amount += voucher.getPrice();
            assertThat(voucher.getOrder()).isEqualTo(orders.get(0));
            assertThat(voucher.getStatus()).isSameAs(ORDER_PLACED);
        }

        assertThat(orders.get(0).getQuantity()).isEqualTo(quantity);
        assertThat(orders.get(0).getAmount()).isEqualTo(amount);

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
    @DisplayName("주문 조회 요청 API 테스트")
    void findOrder() throws Exception {
        // given
        String orderNumber = UUID.randomUUID().toString();
        Order order = new Order(orderNumber, buyer);
        int quantity = 0;
        long amount = 0L;

        for (Voucher voucher : voucherRepository.findAll()) {
            quantity++;
            amount += voucher.getPrice();
            voucher.updateOrder(order);
            voucher.updateStatus(ORDER_PLACED);
        }

        order.updateQuantity(quantity);
        order.updateAmount(amount);
        orderRepository.save(order);

        // when
        ResultActions response = mockMvc.perform(get("/api/orders/{orderNumber}", order.getOrderNumber())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(buyerTokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("orderNumber").value(orderNumber))
                .andExpect(jsonPath("status").value(IN_PROGRESS.name()))
                .andExpect(jsonPath("customerName").value(buyer.getUsername()))
                .andExpect(jsonPath("quantity").value(quantity))
                .andExpect(jsonPath("totalPrice").value(amount))
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
                                fieldWithPath("orderItems.[].voucherId").type(JsonFieldType.NUMBER).description("기프티콘 id"),
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
    @DisplayName("주문 취소 요청 API 테스트")
    void cancelOrder() throws Exception {
        // given
        String orderNumber = UUID.randomUUID().toString();
        Order order = orderRepository.save(new Order(orderNumber, buyer));

        voucherRepository.findAll()
                .forEach(voucherForSale -> voucherForSale.updateOrder(order));

        // when
        ResultActions response = mockMvc.perform(delete("/api/orders/{orderNumber}", order.getOrderNumber())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(buyerTokenInfo)));

        // then
        Optional<Order> orderFound = orderRepository.findById(order.getId());
        List<Voucher> voucherList = voucherRepository.findAllByOrder(order);
        assertThat(orderFound).isNotPresent();
        assertThat(voucherList).isEmpty();

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