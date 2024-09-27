package com.givemecon.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.ControllerTestEnvironment;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucherStatus;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.entity.voucher.VoucherStatus;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.givemecon.application.dto.MemberDto.TokenRequest;
import static com.givemecon.application.dto.PurchasedVoucherDto.PurchasedVoucherRequest;
import static com.givemecon.application.dto.PurchasedVoucherDto.PurchasedVoucherRequestList;
import static com.givemecon.domain.entity.member.Role.USER;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.util.ApiDocumentUtils.*;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PurchasedVoucherControllerTest extends ControllerTestEnvironment {

    Member member;

    VoucherKind voucherKind;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup() {
        member = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(USER)
                .build());

        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageUrl("imageUrl")
                .imageKey("imageKey")
                .originalName("originalName")
                .build());

        voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .voucherKindImage(voucherKindImage)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
    }

    @Test
    void saveAll() throws Exception {
        // given
        Member seller = memberRepository.save(Member.builder()
                .username("seller")
                .email("seller@gmail.com")
                .role(USER)
                .build());

        List<PurchasedVoucherRequest> dtoList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("image" + i + ".png")
                    .build());

            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now().plusDays(1))
                    .voucherImage(voucherImage)
                    .voucherKind(voucherKind)
                    .seller(seller)
                    .build());

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
                                fieldWithPath("requests.[].voucherId").type(JsonFieldType.NUMBER).description("구매할 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("[].voucherId").type(JsonFieldType.NUMBER).description("Voucher id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("[].voucherKindImageUrl").type(JsonFieldType.STRING).description("기프티콘 종류 이미지"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("image" + i + ".png")
                    .build());

            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now().plusDays(1))
                    .voucherImage(voucherImage)
                    .voucherKind(voucherKind)
                    .build());

            purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/purchased-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("[].voucherId").type(JsonFieldType.NUMBER).description("Voucher id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("[].voucherKindImageUrl").type(JsonFieldType.STRING).description("기프티콘 종류 이미지"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );

        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();
        purchasedVoucherList.forEach(purchasedVoucher -> assertThat(purchasedVoucher.getOwner()).isEqualTo(member));
    }

    @Test
    void findOne() throws Exception {
        // given
        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now().plusDays(1))
                .voucherImage(voucherImage)
                .voucherKind(voucherKind)
                .build());

        PurchasedVoucher purchasedVoucher =
                purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));

        // when
        ResultActions response = mockMvc.perform(get("/api/purchased-vouchers/{id}", purchasedVoucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(purchasedVoucher.getId()))
                .andExpect(jsonPath("voucherId").value(voucher.getId()))
                .andExpect(jsonPath("title").value(voucher.getTitle()))
                .andExpect(jsonPath("voucherKindImageUrl").value(voucherImage.getImageUrl()))
                .andExpect(jsonPath("price").value(voucher.getPrice()))
                .andExpect(jsonPath("expDate").value(voucher.getExpDate().toString()))
                .andExpect(jsonPath("status").value(purchasedVoucher.getStatus().name()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("구매할 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("구매한 기프티콘 id"),
                                fieldWithPath("voucherId").type(JsonFieldType.NUMBER).description("Voucher id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("구매한 기프티콘 타이틀"),
                                fieldWithPath("voucherKindImageUrl").type(JsonFieldType.STRING).description("기프티콘 종류 이미지"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("구매한 기프티콘 가격"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("구매한 기프티콘 유효기한"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("기프티콘 사용 여부")
                        ))
                );
    }

    @Test
    void setUsed() throws Exception {
        // given
        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("image.png")
                .build());

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now().plusDays(1))
                .voucherImage(voucherImage)
                .voucherKind(voucherKind)
                .build());

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