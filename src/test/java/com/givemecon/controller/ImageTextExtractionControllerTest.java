package com.givemecon.controller;

import com.givemecon.application.dto.MemberDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.brand.BrandIconRepository;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
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
import org.springframework.web.context.WebApplicationContext;

import java.net.URL;

import static com.givemecon.domain.entity.member.Role.USER;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
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
class ImageTextExtractionControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryIconRepository categoryIconRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    BrandIconRepository brandIconRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

        Member user = memberRepository.save(Member.builder()
                .email("user@gmail.com")
                .username("user")
                .role(USER)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new MemberDto.TokenRequest(user));

        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("categoryIcon")
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build());

        BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("brandIcon")
                .build());

        brandRepository.save(Brand.builder()
                .name("brand")
                .brandIcon(brandIcon)
                .category(category)
                .build());
    }

    @Test
    void extractTextFromImage(@Value("${gcp.test-voucher-image.url}") String imageUrl) throws Exception {
        // given
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", new URL(imageUrl).openStream());

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
                                fieldWithPath("brandName").type(JsonFieldType.STRING).optional().description("추출된 기프티콘 이미지의 브랜드명"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).optional().description("추출된 기프티콘 이미지의 유효기간"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).optional().description("추출된 기프티콘 이미지의 바코드")
                        ))
                );
    }
}