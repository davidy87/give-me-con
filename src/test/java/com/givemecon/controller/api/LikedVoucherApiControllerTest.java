package com.givemecon.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.image.voucherkind.VoucherKindImage;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucherkind.VoucherKind;
import com.givemecon.domain.image.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.voucherkind.VoucherKindRepository;
import com.givemecon.domain.likedvoucher.LikedVoucher;
import com.givemecon.domain.likedvoucher.LikedVoucherRepository;
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

import java.util.List;

import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.controller.ApiDocumentUtils.*;
import static com.givemecon.controller.TokenHeaderUtils.getAccessTokenHeader;
import static com.givemecon.domain.member.MemberDto.*;
import static com.givemecon.domain.voucherkind.VoucherKindDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
class LikedVoucherApiControllerTest {

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
    MemberRepository memberRepository;

    @Autowired
    LikedVoucherRepository likedVoucherRepository;

    Member member;

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

        tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
    }

    @Test
    void save() throws Exception {
        // given
        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .description("voucherKind description")
                .caution("voucherKind caution")
                .build());

        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("voucherKindImage.png")
                .build());

        voucherKind.updateVoucherImage(voucherKindImage);

        // when
        ResultActions response = mockMvc.perform(post("/api/liked-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(voucherKind.getId())));

        // then
        VoucherKindResponse voucherKindResponse = new VoucherKindResponse(voucherKind);

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherKindResponse.getId()))
                .andExpect(jsonPath("minPrice").value(voucherKindResponse.getMinPrice()))
                .andExpect(jsonPath("title").value(voucherKindResponse.getTitle()))
                .andExpect(jsonPath("imageUrl").value(voucherKindResponse.getImageUrl()))
                .andExpect(jsonPath("description").value(voucherKindResponse.getDescription()))
                .andExpect(jsonPath("caution").value(voucherKindResponse.getCaution()))
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
        assertThat(found.getMember()).isEqualTo(member);
    }

    @Test
    @WithMockUser(roles = "USER", username = "tester")
    void findAllByUsername() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                    .title("voucherKind" + i)
                    .description("voucherKind" + i + " description")
                    .caution("voucherKind" + i + " caution")
                    .build());

            VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherKindImage" + i + ".png")
                    .build());

            voucherKind.updateVoucherImage(voucherKindImage);
            likedVoucherRepository.save(new LikedVoucher(member, voucherKind));
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/liked-vouchers"));

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
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 종류의 이미지"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("[].caution").type(JsonFieldType.STRING).description("사용 시 유의사항")
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
                        .member(member)
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