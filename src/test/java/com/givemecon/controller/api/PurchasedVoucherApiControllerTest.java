package com.givemecon.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImageRepository;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.controller.ApiDocumentUtils.*;
import static com.givemecon.controller.TokenHeaderUtils.getAccessTokenHeader;
import static com.givemecon.domain.member.MemberDto.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
class PurchasedVoucherApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    VoucherForSaleImageRepository voucherForSaleImageRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Autowired
    MemberRepository memberRepository;

    Member member;

    Voucher voucher;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

        member = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .authority(USER)
                .build());

        voucher = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
    }

    @Test
    void saveAll() throws Exception {
        // given
        List<PurchasedVoucherRequest> dtoList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            VoucherForSale voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now().plusDays(1))
                    .build());

            VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("image" + i + ".png")
                    .build());

            voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
            voucherForSale.updateVoucher(voucher);
            voucherForSale.updateStatus(FOR_SALE);
            dtoList.add(new PurchasedVoucherRequest(voucherForSale.getId()));
        }

        PurchasedVoucherRequestList requestDtoList = new PurchasedVoucherRequestList(dtoList);

        // when
        ResultActions response = mockMvc.perform(post("/api/purchased-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDtoList)));

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("requests.[].voucherForSaleId").type(JsonFieldType.NUMBER).description("구매할 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("구매한 기프티콘 이미지"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );

        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();
        assertThat(purchasedVoucherList).hasSize(requestDtoList.getRequests().size());
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        for (int i = 1; i <= 20; i++) {
            VoucherForSale voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now().plusDays(1))
                    .build());

            VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("image" + i + ".png")
                    .build());

            PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(new PurchasedVoucher());

            voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
            voucherForSale.updateVoucher(voucher);
            purchasedVoucher.updateVoucherForSale(voucherForSale);
            purchasedVoucher.updateOwner(member);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/purchased-vouchers")
                .queryParam("page", "1")
                .queryParam("size", "10")
                .queryParam("sort", "id")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pagingQueryParameters(),
                        responseFields(
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 번호"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("현재 페이지의 항목 수"),
                                fieldWithPath("purchasedVouchers").type(JsonFieldType.ARRAY).description("현재 페이지의 기프티콘 구매 목록"),
                                fieldWithPath("purchasedVouchers.[].id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("purchasedVouchers.[].title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("purchasedVouchers.[].imageUrl").type(JsonFieldType.STRING).description("구매한 기프티콘 이미지"),
                                fieldWithPath("purchasedVouchers.[].price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("purchasedVouchers.[].expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("purchasedVouchers.[].barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("purchasedVouchers.[].status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );

        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();
        purchasedVoucherList.forEach(purchasedVoucher -> assertThat(purchasedVoucher.getOwner()).isEqualTo(member));
    }

    @Test
    void findOne() throws Exception {
        // given
        VoucherForSale voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now().plusDays(1))
                .build());

        VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(new PurchasedVoucher());

        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
        voucherForSale.updateVoucher(voucher);
        purchasedVoucher.updateVoucherForSale(voucherForSale);
        purchasedVoucher.updateOwner(member);

        // when
        ResultActions response = mockMvc.perform(get("/api/purchased-vouchers/{id}", purchasedVoucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(purchasedVoucher.getId()))
                .andExpect(jsonPath("title").value(voucherForSale.getTitle()))
                .andExpect(jsonPath("imageUrl").value(voucherForSaleImage.getImageUrl()))
                .andExpect(jsonPath("price").value(voucherForSale.getPrice()))
                .andExpect(jsonPath("expDate").value(voucherForSale.getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(voucherForSale.getBarcode()))
                .andExpect(jsonPath("status").value(purchasedVoucher.getStatus().name()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("구매할 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("구매한 기프티콘 이미지"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );
    }

    @Test
    void setUsed() throws Exception {
        // given
        VoucherForSale voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now().plusDays(1))
                .build());

        VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(new PurchasedVoucher());

        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
        voucherForSale.updateVoucher(voucher);
        purchasedVoucher.updateVoucherForSale(voucherForSale);
        purchasedVoucher.updateOwner(member);

        // when
        ResultActions response = mockMvc.perform(put("/api/purchased-vouchers/{id}", purchasedVoucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(purchasedVoucher.getId()))
                .andExpect(jsonPath("title").value(voucherForSale.getVoucher().getTitle()))
                .andExpect(jsonPath("imageUrl").value(voucherForSaleImage.getImageUrl()))
                .andExpect(jsonPath("price").value(voucherForSale.getPrice()))
                .andExpect(jsonPath("expDate").value(voucherForSale.getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(voucherForSale.getBarcode()))
                .andExpect(jsonPath("status").value(purchasedVoucher.getStatus().name()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("구매한 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("구매한 기프티콘 이미지"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );
    }
}