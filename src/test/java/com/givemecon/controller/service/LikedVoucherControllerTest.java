package com.givemecon.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.application.dto.VoucherKindDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.ControllerTestEnvironment;
import com.givemecon.domain.entity.likedvoucher.LikedVoucher;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.givemecon.application.dto.MemberDto.TokenRequest;
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

class LikedVoucherControllerTest extends ControllerTestEnvironment {

    Member user;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup() {
        user = memberRepository.save(Member.builder()
                .email("user@gmail.com")
                .username("user")
                .role(USER)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(user));
    }

    @Test
    void save() throws Exception {
        // given
        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("voucherKindImage.png")
                .build());

        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .description("voucherKind description")
                .caution("voucherKind caution")
                .voucherKindImage(voucherKindImage)
                .build());

        // when
        ResultActions response = mockMvc.perform(post("/api/liked-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(voucherKind.getId())));

        // then
        VoucherKindDto.VoucherKindDetailResponse voucherKindDetailResponse = new VoucherKindDto.VoucherKindDetailResponse(voucherKind);

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherKindDetailResponse.getId()))
                .andExpect(jsonPath("minPrice").value(voucherKindDetailResponse.getMinPrice()))
                .andExpect(jsonPath("title").value(voucherKindDetailResponse.getTitle()))
                .andExpect(jsonPath("imageUrl").value(voucherKindDetailResponse.getImageUrl()))
                .andExpect(jsonPath("description").value(voucherKindDetailResponse.getDescription()))
                .andExpect(jsonPath("caution").value(voucherKindDetailResponse.getCaution()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestBody(),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("minPrice").type(JsonFieldType.NUMBER).description("기프티콘 종류의 최소 가격"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("기프티콘 종류의 타이틀"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("기프티콘 종류의 이미지"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("caution").type(JsonFieldType.STRING).description("사용 시 유의사항")
                        ))
                );

        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();
        LikedVoucher found = likedVoucherList.get(0);
        assertThat(found.getVoucherKind()).isEqualTo(voucherKind);
        assertThat(found.getMember()).isEqualTo(user);
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherKindImage" + i + ".png")
                    .build());

            VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                    .title("voucherKind" + i)
                    .description("voucherKind" + i + " description")
                    .caution("voucherKind" + i + " caution")
                    .voucherKindImage(voucherKindImage)
                    .build());

            likedVoucherRepository.save(new LikedVoucher(user, voucherKind));
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/liked-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pagingQueryParameters(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 종류의 id"),
                                fieldWithPath("[].minPrice").type(JsonFieldType.NUMBER).description("기프티콘 종류의 최소 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 종류의 타이틀"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 종류의 이미지")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        VoucherKind voucherKindSaved = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .build());

        LikedVoucher likedVoucherSaved = likedVoucherRepository.save(
                LikedVoucher.builder()
                        .member(user)
                        .voucherKind(voucherKindSaved)
                        .build());

        // when
        ResultActions response = mockMvc.perform(delete("/api/liked-vouchers/{voucherId}", voucherKindSaved.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("voucherId").description("기프티콘 종류의 id")
                        ))
                );

        assertThat(likedVoucherRepository.existsById(likedVoucherSaved.getId())).isFalse();
    }
}