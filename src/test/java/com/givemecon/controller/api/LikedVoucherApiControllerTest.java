package com.givemecon.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.image.voucher.VoucherImageRepository;
import com.givemecon.domain.voucher.VoucherRepository;
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
    VoucherRepository voucherRepository;

    @Autowired
    VoucherImageRepository voucherImageRepository;

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
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .description("voucher description")
                .caution("voucher caution")
                .build());

        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("voucherImage.png")
                .build());

        voucher.updateVoucherImage(voucherImage);

        // when
        ResultActions response = mockMvc.perform(post("/api/liked-vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(voucher.getId())));

        // then
        response.andExpect(status().isCreated())
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
        assertThat(found.getVoucher()).isEqualTo(voucher);
        assertThat(found.getMember()).isEqualTo(member);
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        for (int i = 1; i <= 20; i++) {
            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .title("voucher" + i)
                    .description("voucher" + i + " description")
                    .caution("voucher" + i + " caution")
                    .build());

            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherImage" + i + ".png")
                    .build());

            voucher.updateVoucherImage(voucherImage);
            LikedVoucher likedVoucher = likedVoucherRepository.save(new LikedVoucher(voucher));
            likedVoucher.updateMember(member);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/liked-vouchers")
                .queryParam("page", "1")
                .queryParam("size", "10")
                .queryParam("sort", "id")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pagingQueryParameters(),
                        responseFields(
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 번호"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("현재 페이지의 항목 수"),
                                fieldWithPath("vouchers").type(JsonFieldType.ARRAY).description("현재 페이지의 기프티콘 종류 목록"),
                                fieldWithPath("vouchers.[].id").type(JsonFieldType.NUMBER).description("기프티콘 종류의 id"),
                                fieldWithPath("vouchers.[].minPrice").type(JsonFieldType.NUMBER).description("기프티콘 종류의 최소 가격"),
                                fieldWithPath("vouchers.[].title").type(JsonFieldType.STRING).description("기프티콘 종류의 타이틀"),
                                fieldWithPath("vouchers.[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 종류의 이미지"),
                                fieldWithPath("vouchers.[].description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("vouchers.[].caution").type(JsonFieldType.STRING).description("사용 시 유의사항")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        Voucher voucher = Voucher.builder()
                .title("voucher")
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);
        LikedVoucher likedVoucherSaved = likedVoucherRepository.save(LikedVoucher.builder().voucher(voucherSaved).build());
        likedVoucherSaved.updateMember(member);

        // when
        ResultActions response = mockMvc.perform(delete("/api/liked-vouchers/{voucherId}", voucherSaved.getId())
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