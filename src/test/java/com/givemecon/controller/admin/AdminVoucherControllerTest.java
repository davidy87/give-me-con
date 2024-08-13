package com.givemecon.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.application.dto.MemberDto;
import com.givemecon.application.dto.VoucherDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.domain.entity.member.Role.ADMIN;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;
import static com.givemecon.domain.entity.voucher.VoucherStatus.SALE_REQUESTED;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.assertj.core.api.Assertions.assertThat;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@Transactional
@SpringBootTest
class AdminVoucherControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    TokenInfo tokenInfo;

    VoucherKind voucherKind;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

        Member admin = memberRepository.save(Member.builder()
                .email("admin@gmail.com")
                .username("admin")
                .role(ADMIN)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new MemberDto.TokenRequest(admin));

        voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .build());
    }

    @Test
    @DisplayName("Admin용 VoucherStatus별 Voucher 조회 요청 API 테스트")
    void findAllByStatus() throws Exception {
        // given
        List<Voucher> toSaveList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Voucher toSave = Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now())
                    .voucherKind(voucherKind)
                    .build();

            if (i < 2) {
                toSave.updateStatus(FOR_SALE);
            }

            toSaveList.add(toSave);
        }

        voucherRepository.saveAll(toSaveList);

        // when
        ResultActions response = mockMvc.perform(get("/api/admin/vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .queryParam("statusCode", String.valueOf(SALE_REQUESTED.ordinal())));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("statusCode").description("기프티콘 상태코드 (0 ~ 5)")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("판매 기프티콘 id"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("판매 기프티콘 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("판매 기프티콘 타이틀"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("판매 기프티콘 바코드"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("판매 기프티콘 유효기간"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("판매 기프티콘 상태"),
                                fieldWithPath("[].saleRequestedDate").type(JsonFieldType.STRING).description("기프티콘 판매 요청일자")
                        ))
                );
    }

    @Test
    @DisplayName("Admin용 Voucher 상태 수정 요청 API 테스트")
    void updateStatus() throws Exception {
        // given
        Voucher voucher = Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .voucherKind(voucherKind)
                .build();

        voucherRepository.save(voucher);

        // when
        VoucherDto.StatusUpdateRequest requestBody = new VoucherDto.StatusUpdateRequest();
        requestBody.setStatusCode(FOR_SALE.ordinal());

        ResultActions response = mockMvc.perform(put("/api/admin/vouchers/{id}", voucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucher.getId()))
                .andExpect(jsonPath("price").value(voucher.getPrice()))
                .andExpect(jsonPath("title").value(voucher.getTitle()))
                .andExpect(jsonPath("barcode").value(voucher.getBarcode()))
                .andExpect(jsonPath("expDate").value(voucher.getExpDate().toString()))
                .andExpect(jsonPath("status").value(FOR_SALE.name()))
                .andExpect(jsonPath("saleRequestedDate").value(voucher.getSaleRequestedDate().toString()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("판매중(or 판매 대기 중)인 기프티콘 id")
                        ),
                        requestFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("기프티콘 상태코드 (0 ~ 4)"),
                                fieldWithPath("rejectedReason")
                                        .type(JsonFieldType.STRING).optional()
                                        .description("판매 요청 거절 사유 (statusCode가 3(SALE_REJECTED)일 경우 필수)")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("판매 기프티콘 id"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("판매 기프티콘 가격"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("판매 기프티콘 타이틀"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("판매 기프티콘 바코드"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("판매 기프티콘 유효기간"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("판매 기프티콘 상태"),
                                fieldWithPath("saleRequestedDate").type(JsonFieldType.STRING).description("기프티콘 판매 요청일자")
                        ))
                );
    }

    @Test
    @DisplayName("Admin용 Voucher 삭제 API 테스트")
    void deleteOne() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .expDate(LocalDate.now().plusDays(1))
                .barcode("1111 1111 1111")
                .build());

        // when
        ResultActions response = mockMvc.perform(delete("/api/admin/vouchers/{id}", voucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("판매중인 기프티콘 id")
                        ))
                );

        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList).isEmpty();
    }
}