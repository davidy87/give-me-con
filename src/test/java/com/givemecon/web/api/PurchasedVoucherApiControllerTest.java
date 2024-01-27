package com.givemecon.web.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.PurchasedVoucherRepository;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.givemecon.web.ApiDocumentUtils.*;
import static com.givemecon.web.dto.PurchasedVoucherDto.*;
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
@WithMockUser(roles = "USER", username = "tester")
class PurchasedVoucherApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();
    }

    @Test
    void saveAll() throws Exception {
        // given
        Voucher voucherSaved = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .price(4_000L)
                .build());

        Member memberSaved = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        List<PurchasedVoucherRequest> dtoList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            PurchasedVoucherRequest requestDto = PurchasedVoucherRequest.builder()
                    .image("voucher" + i + ".png")
                    .title("voucher" + i)
                    .price(4_000L)
                    .expDate(LocalDate.now().plusDays(1))
                    .barcode("1111 1111 1111")
                    .voucherId(voucherSaved.getId())
                    .build();

            dtoList.add(requestDto);
        }

        PurchasedVoucherRequestList requestDtoList = new PurchasedVoucherRequestList(dtoList);
        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);

        // when
        ResultActions response = mockMvc.perform(post("/api/purchased-vouchers")
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(requestDtoList)));

        // then
        response.andExpect(status().isCreated())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("requestList.[].title").type(JsonFieldType.STRING).description("구매할 기프티콘 타이틀"),
                                fieldWithPath("requestList.[].image").type(JsonFieldType.STRING).description("구매할 기프티콘 이미지"),
                                fieldWithPath("requestList.[].price").type(JsonFieldType.NUMBER).description("구매할 기프티콘 가격"),
                                fieldWithPath("requestList.[].expDate").type(JsonFieldType.ARRAY).description("구매할 기프티콘 유효기간"),
                                fieldWithPath("requestList.[].barcode").type(JsonFieldType.STRING).description("구매할 기프티콘 바코드"),
                                fieldWithPath("requestList.[].voucherId").type(JsonFieldType.NUMBER).description("구매할 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("[].image").type(JsonFieldType.STRING).description("구매한 기프티콘 이미지"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기간"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("[].valid").type(JsonFieldType.BOOLEAN).description("기프티콘 유효 여부")
                        ))
                );

        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();
        assertThat(purchasedVoucherList).hasSize(requestDtoList.getRequestList().size());
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        Member owner = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        List<PurchasedVoucher> entityList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            PurchasedVoucher purchasedVoucher = PurchasedVoucher.builder()
                    .image("voucher" + i + ".png")
                    .title("voucher" + i)
                    .price(4_000L)
                    .expDate(LocalDate.now().plusDays(1))
                    .barcode("1111 1111 1111")
                    .build();

            owner.addPurchasedVoucher(purchasedVoucher);
            entityList.add(purchasedVoucher);
        }

        purchasedVoucherRepository.saveAll(entityList);
        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(owner);

        // when
        ResultActions response = mockMvc.perform(get("/api/purchased-vouchers")
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("[].image").type(JsonFieldType.STRING).description("구매한 기프티콘 이미지"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기간"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("[].valid").type(JsonFieldType.BOOLEAN).description("기프티콘 유효 여부")
                        ))
                );

        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();
        assertThat(purchasedVoucherList).hasSize(entityList.size());
        purchasedVoucherList.forEach(purchasedVoucher -> assertThat(purchasedVoucher.getOwner()).isEqualTo(owner));
    }

    @Test
    void findOne() throws Exception {
        // given
        Member owner = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(PurchasedVoucher.builder()
                .image("voucher.png")
                .title("voucher")
                .price(4_000L)
                .expDate(LocalDate.now().plusDays(1))
                .barcode("1111 1111 1111")
                .build());

        owner.addPurchasedVoucher(purchasedVoucher);
        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(owner);

        // when
        ResultActions response = mockMvc.perform(get("/api/purchased-vouchers/{id}", purchasedVoucher.getId())
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(purchasedVoucher.getId()))
                .andExpect(jsonPath("image").value(purchasedVoucher.getImage()))
                .andExpect(jsonPath("title").value(purchasedVoucher.getTitle()))
                .andExpect(jsonPath("price").value(purchasedVoucher.getPrice()))
                .andExpect(jsonPath("expDate").value(purchasedVoucher.getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(purchasedVoucher.getBarcode()))
                .andExpect(jsonPath("valid").value(purchasedVoucher.getValid()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("구매한 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("구매한 기프티콘 이미지"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기간"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("valid").type(JsonFieldType.BOOLEAN).description("기프티콘 유효 여부")
                        ))
                );
    }

    @Test
    void updateValidity() throws Exception {
        // given
        Member owner = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(PurchasedVoucher.builder()
                .image("voucher.png")
                .title("voucher")
                .price(4_000L)
                .expDate(LocalDate.now().plusDays(1))
                .barcode("1111 1111 1111")
                .build());

        owner.addPurchasedVoucher(purchasedVoucher);
        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(owner);

        // when
        ResultActions response = mockMvc.perform(put("/api/purchased-vouchers/{id}", purchasedVoucher.getId())
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(purchasedVoucher.getId()))
                .andExpect(jsonPath("image").value(purchasedVoucher.getImage()))
                .andExpect(jsonPath("title").value(purchasedVoucher.getTitle()))
                .andExpect(jsonPath("price").value(purchasedVoucher.getPrice()))
                .andExpect(jsonPath("expDate").value(purchasedVoucher.getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(purchasedVoucher.getBarcode()))
                .andExpect(jsonPath("valid").value(purchasedVoucher.getValid()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("구매한 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("구매한 기프티콘 이미지"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기간"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("구매한 기프티콘 바코드"),
                                fieldWithPath("valid").type(JsonFieldType.BOOLEAN).description("기프티콘 유효 여부")
                        ))
                );
    }
}