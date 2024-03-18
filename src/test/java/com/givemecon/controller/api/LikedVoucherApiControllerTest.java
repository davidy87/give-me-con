package com.givemecon.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.enums.Role;
import com.givemecon.config.auth.jwt.token.JwtTokenProvider;
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

import static com.givemecon.controller.ApiDocumentUtils.*;
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
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherImageRepository voucherImageRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LikedVoucherRepository likedVoucherRepository;

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
    void save() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .price(4_000L)
                .build());

        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("voucherImage.png")
                .build());

        Member member = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        voucher.updateVoucherImage(voucherImage);
        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(member);

        // when
        ResultActions response = mockMvc.perform(post("/api/liked-vouchers")
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
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
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("기프티콘 이미지")
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
        Member member = Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Member memberSaved = memberRepository.save(member);

        for (int i = 1; i <= 5; i++) {
            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .title("voucher" + i)
                    .price(4_000L)
                    .build());

            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherImage" + i + ".png")
                    .build());

            voucher.updateVoucherImage(voucherImage);
            memberSaved.addLikedVoucher(new LikedVoucher(voucher));
        }

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);

        // when
        ResultActions response = mockMvc.perform(get("/api/liked-vouchers")
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 이미지")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        Voucher voucher = Voucher.builder()
                .title("voucher")
                .price(4_000L)
                .build();

        Member member = Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);
        Member memberSaved = memberRepository.save(member);
        LikedVoucher likedVoucherSaved = likedVoucherRepository.save(LikedVoucher.builder().voucher(voucherSaved).build());
        likedVoucherSaved.updateMember(member);
        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);

        // when
        ResultActions response = mockMvc.perform(delete("/api/liked-vouchers/{voucherId}", voucherSaved.getId())
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("voucherId").description("기프티콘 id")
                        ))
                );

        assertThat(likedVoucherRepository.existsById(likedVoucherSaved.getId())).isFalse();
    }
}