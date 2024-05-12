package com.givemecon.controller.api;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberDto;
import com.givemecon.domain.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;

import static com.givemecon.config.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.config.enums.Authority.USER;
import static com.givemecon.controller.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.controller.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.controller.TokenHeaderUtils.getAccessTokenHeader;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
class ImageTextExtractionApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    JwtTokenService jwtTokenService;

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
                .email("test@gmail.com")
                .username("tester")
                .authority(USER)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new MemberDto.TokenRequest(member));
    }

    @Test
    void extractTextFromImage(@Value("${gcp.test-voucher-image.path}") String testImage) throws Exception {
        // given
        brandRepository.save(Brand.builder()
                .name("배스킨라빈스")
                .build());

        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                new FileInputStream(ResourceUtils.getFile(testImage)));

        // when
        ResultActions response =
                mockMvc.perform(multipart("/api/images/extracted-texts")
                        .file(imageFile)
                        .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("imageFile").description("텍스트를 추출할 기프티콘 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("brandName").optional().type(JsonFieldType.STRING).description("추출된 기프티콘 이미지의 브랜드명"),
                                fieldWithPath("expDate").optional().type(JsonFieldType.STRING).description("추출된 기프티콘 이미지의 유효기간"),
                                fieldWithPath("barcode").optional().type(JsonFieldType.STRING).description("추출된 기프티콘 이미지의 바코드")
                        ))
                );
    }

}