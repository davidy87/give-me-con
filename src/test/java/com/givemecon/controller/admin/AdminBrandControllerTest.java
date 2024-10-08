package com.givemecon.controller.admin;

import com.givemecon.application.dto.MemberDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.ControllerTestEnvironment;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.entity.member.Member;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.givemecon.application.exception.errorcode.BrandErrorCode.INVALID_BRAND_ID;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.domain.entity.member.Role.ADMIN;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminBrandControllerTest extends ControllerTestEnvironment {

    Category category;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup() {
        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("categoryIcon")
                .build());

        category = categoryRepository.save(Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build());

        Member admin = memberRepository.save(Member.builder()
                .email("admin@gmail.com")
                .username("admin")
                .role(ADMIN)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new MemberDto.TokenRequest(admin));
    }

    @Test
    void save() throws Exception {
        // given
        String name = "Brand";
        MockMultipartFile iconFile = new MockMultipartFile(
                "iconFile",
                "brandIcon.png",
                "image/png",
                "brandIcon.png".getBytes());

        // when
        ResultActions result = mockMvc.perform(multipart("/api/admin/brands")
                .file(iconFile)
                .part(new MockPart("categoryId", String.valueOf(category.getId()).getBytes(StandardCharsets.UTF_8)))
                .part(new MockPart("name", name.getBytes(StandardCharsets.UTF_8)))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).isNotEmpty();

        Brand brand = brandList.get(0);
        assertThat(brand.getCategory()).isEqualTo(category);

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(brand.getId()))
                .andExpect(jsonPath("name").value(brand.getName()))
                .andExpect(jsonPath("iconUrl").value(brand.getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("categoryId").description("설정할 카테고리 id"),
                                partWithName("name").description("저장할 브랜드 이름"),
                                partWithName("iconFile").description("저장할 브랜드 아이콘 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 브랜드 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("저장된 브랜드 이름"),
                                fieldWithPath("iconUrl").type(JsonFieldType.STRING).description("저장된 브랜드 아이콘")
                        ))
                );
    }

    @Test
    void update() throws Exception {
        // given
        CategoryIcon newCategoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("newImageKey")
                .imageUrl("newImageUrl")
                .originalName("newCategoryIcon")
                .build());

        Category newCategory = categoryRepository.save(Category.builder()
                .name("new")
                .categoryIcon(newCategoryIcon)
                .build());

        BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("brandIcon.jpg")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("oldBrand")
                .brandIcon(brandIcon)
                .category(category)
                .build());

        String newName = "newBrand";
        MockMultipartFile newIconFile = new MockMultipartFile(
                "iconFile",
                "newBrand.png",
                "image/png",
                "newBrand.png".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/admin/brands/{id}", brand.getId())
                .file(newIconFile)
                .part(new MockPart("categoryId", newCategory.getId().toString().getBytes()))
                .part(new MockPart("name", newName.getBytes()))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).isNotEmpty();

        Brand updatedBrand = brandList.get(0);
        assertThat(updatedBrand.getCategory()).isSameAs(newCategory);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(updatedBrand.getId()))
                .andExpect(jsonPath("name").value(updatedBrand.getName()))
                .andExpect(jsonPath("iconUrl").value(updatedBrand.getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("브랜드 id")
                        ),
                        requestParts(
                                partWithName("categoryId").description("변경할 브랜드 카테고리 id").optional(),
                                partWithName("name").description("변경할 브랜드 이름").optional(),
                                partWithName("iconFile").description("변경할 브랜드 아이콘 파일").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("변경된 브랜드 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 브랜드명"),
                                fieldWithPath("iconUrl").type(JsonFieldType.STRING).description("변경된 브랜드 아이콘")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("brandIcon.png")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("Brand")
                .brandIcon(brandIcon)
                .category(category)
                .build());

        // when
        ResultActions response = mockMvc.perform(delete("/api/admin/brands/{id}", brand.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("브랜드 id")
                        ))
                );

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).isEmpty();
    }

    @Nested
    @DisplayName("Brand API 예외 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("Brand Id 예외 - 올바르지 않은 Brand Id")
        void brandsExceptionTest() throws Exception {
            // given
            String newName = "newCategory";
            MockMultipartFile newIconFile = new MockMultipartFile(
                    "icon",
                    "new_brand.jpg",
                    "image/jpg",
                    "new_brand.jpg".getBytes());

            Long invalidBrandId = 1L;

            // when
            ResultActions response =
                    mockMvc.perform(multipart("/api/admin/brands/{id}", invalidBrandId)
                            .file(newIconFile)
                            .part(new MockPart("categoryId", String.valueOf(category.getId()).getBytes(StandardCharsets.UTF_8)))
                            .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8)))
                            .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

            // then
            response.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("error.status").value(INVALID_BRAND_ID.getStatus()))
                    .andExpect(jsonPath("error.code").value(INVALID_BRAND_ID.getCode()))
                    .andExpect(jsonPath("error.message").value(INVALID_BRAND_ID.getMessage()));
        }
    }
}