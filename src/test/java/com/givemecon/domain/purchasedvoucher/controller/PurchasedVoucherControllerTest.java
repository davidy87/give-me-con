package com.givemecon.domain.purchasedvoucher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.image.entity.VoucherImage;
import com.givemecon.domain.image.entity.VoucherKindImage;
import com.givemecon.domain.image.repository.VoucherKindImageRepository;
import com.givemecon.domain.member.entity.Member;
import com.givemecon.domain.member.repository.MemberRepository;
import com.givemecon.domain.purchasedvoucher.dto.PurchasedVoucherStatus;
import com.givemecon.domain.voucher.entity.Voucher;
import com.givemecon.domain.voucherkind.entity.VoucherKind;
import com.givemecon.domain.voucherkind.repository.VoucherKindRepository;
import com.givemecon.domain.purchasedvoucher.entity.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.repository.PurchasedVoucherRepository;
import com.givemecon.domain.image.repository.VoucherForSaleImageRepository;
import com.givemecon.domain.voucher.repository.VoucherRepository;
import com.givemecon.domain.voucher.dto.VoucherStatus;
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
import static com.givemecon.util.ApiDocumentUtils.*;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static com.givemecon.domain.member.dto.MemberDto.*;
import static com.givemecon.domain.purchasedvoucher.dto.PurchasedVoucherDto.*;
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
class PurchasedVoucherControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Autowired
    VoucherKindImageRepository voucherKindImageRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherForSaleImageRepository voucherForSaleImageRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Autowired
    MemberRepository memberRepository;

    Member member;

    VoucherKind voucherKind;

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

        voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .build());

        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageUrl("imageUrl")
                .imageKey("imageKey")
                .originalName("originalName")
                .build());

        voucherKind.updateVoucherKindImage(voucherKindImage);

        tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
    }

    @Test
    void saveAll() throws Exception {
        // given
        List<PurchasedVoucherRequest> dtoList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now().plusDays(1))
                    .build());

            VoucherImage voucherImage = voucherForSaleImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("image" + i + ".png")
                    .build());

            voucher.updateVoucherImage(voucherImage);
            voucher.updateVoucherKind(voucherKind);
            voucher.updateStatus(VoucherStatus.FOR_SALE);
            dtoList.add(new PurchasedVoucherRequest(voucher.getId()));
        }

        PurchasedVoucherRequestList requestDtoList = new PurchasedVoucherRequestList(dtoList);

        // when
        ResultActions response = mockMvc.perform(post("/api/purchased-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDtoList)));

        // then
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();
        assertThat(purchasedVoucherList).hasSize(requestDtoList.getRequests().size());

        purchasedVoucherList.forEach(purchasedVoucher -> {
            Voucher voucher = purchasedVoucher.getVoucher();
            assertThat(voucher.getStatus()).isEqualTo(VoucherStatus.SOLD);
            assertThat(purchasedVoucher.getStatus()).isEqualTo(PurchasedVoucherStatus.USABLE);
        });

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
                                fieldWithPath("[].voucherKindImageUrl").type(JsonFieldType.STRING).description("기프티콘 종류 이미지"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        for (int i = 1; i <= 20; i++) {
            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now().plusDays(1))
                    .build());

            VoucherImage voucherImage = voucherForSaleImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("image" + i + ".png")
                    .build());


            voucher.updateVoucherImage(voucherImage);
            voucher.updateVoucherKind(voucherKind);
            purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));
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
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("[].voucherKindImageUrl").type(JsonFieldType.STRING).description("기프티콘 종류 이미지"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );

        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();
        purchasedVoucherList.forEach(purchasedVoucher -> assertThat(purchasedVoucher.getOwner()).isEqualTo(member));
    }

    @Test
    void findOne() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now().plusDays(1))
                .build());

        VoucherImage voucherImage = voucherForSaleImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        voucher.updateVoucherImage(voucherImage);
        voucher.updateVoucherKind(voucherKind);
        PurchasedVoucher purchasedVoucher =
                purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));

        // when
        ResultActions response = mockMvc.perform(get("/api/purchased-vouchers/{id}", purchasedVoucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(purchasedVoucher.getId()))
                .andExpect(jsonPath("title").value(voucher.getTitle()))
                .andExpect(jsonPath("voucherKindImageUrl").value(voucherImage.getImageUrl()))
                .andExpect(jsonPath("price").value(voucher.getPrice()))
                .andExpect(jsonPath("expDate").value(voucher.getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(voucher.getBarcode()))
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
                                fieldWithPath("voucherKindImageUrl").type(JsonFieldType.STRING).description("기프티콘 종류 이미지"),
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
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now().plusDays(1))
                .build());

        VoucherImage voucherImage = voucherForSaleImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        voucher.updateVoucherImage(voucherImage);
        voucher.updateVoucherKind(voucherKind);
        PurchasedVoucher purchasedVoucher =
                purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));

        // when
        ResultActions response = mockMvc.perform(put("/api/purchased-vouchers/{id}", purchasedVoucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(purchasedVoucher.getId()))
                .andExpect(jsonPath("status").value(purchasedVoucher.getStatus().name()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("구매한 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );
    }
}