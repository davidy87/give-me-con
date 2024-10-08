package com.givemecon.controller.admin;

import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.ControllerTestEnvironment;
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

import static com.givemecon.application.dto.MemberDto.*;
import static com.givemecon.application.exception.errorcode.CategoryErrorCode.INVALID_CATEGORY_ID;
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

class AdminCategoryControllerTest extends ControllerTestEnvironment {

    TokenInfo tokenInfo;

    @BeforeEach
    void setup() {
        Member admin = memberRepository.save(Member.builder()
                .email("admin@gmail.com")
                .username("admin")
                .role(ADMIN)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(admin));
    }

    @Test
    void save() throws Exception {
        // given
        String name = "category";
        MockMultipartFile iconFile = new MockMultipartFile(
                "iconFile",
                "categoryIcon.png",
                "image/jpg",
                "categoryIcon.png".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/admin/categories")
                .file(iconFile)
                .part(new MockPart("name", name.getBytes(StandardCharsets.UTF_8)))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).isNotEmpty();
        Category category = categoryList.get(0);

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(category.getId()))
                .andExpect(jsonPath("name").value(category.getName()))
                .andExpect(jsonPath("iconUrl").value(category.getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("name").description("저장할 카테고리 이름"),
                                partWithName("iconFile").description("저장할 카테고리 아이콘 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 카테고리 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("저장된 카테고리 이름"),
                                fieldWithPath("iconUrl").type(JsonFieldType.STRING).description("저장된 카테고리 아이콘 URL")
                        ))
                );
    }

    @Test
    void update() throws Exception {
        // given
        String oldName = "old";
        String oldIconName = "oldCategoryIcon.png";
        String imageKey = "imageKey";
        String imageUrl = "imageUrl";

        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .originalName(oldIconName)
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name(oldName)
                .categoryIcon(categoryIcon)
                .build());

        String newName = "new";
        MockMultipartFile newIconFile = new MockMultipartFile(
                "iconFile",
                "newCategoryIcon.png",
                "image/png",
                "newCategoryIcon.png".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/admin/categories/{id}", category.getId())
                .file(newIconFile)
                .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8)))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).isNotEmpty();
        Category updatedCategory = categoryList.get(0);

        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(updatedCategory.getId()))
                .andExpect(jsonPath("name").value(updatedCategory.getName()))
                .andExpect(jsonPath("iconUrl").value(updatedCategory.getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("카테고리 id")
                        ),
                        requestParts(
                                partWithName("name").description("수정할 카테고리 이름").optional(),
                                partWithName("iconFile").description("수정할 카테고리 아이콘 파일").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("변경된 카테고리 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 카테고리 이름"),
                                fieldWithPath("iconUrl").type(JsonFieldType.VARIES).description("변경된 카테고리 아이콘")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        String name = "category";
        String imageKey = "imageKey";
        String imageUrl = "imageUrl";
        String icon = "categoryIcon.png";

        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .originalName(icon)
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name(name)
                .categoryIcon(categoryIcon)
                .build());

        // when
        ResultActions response = mockMvc.perform(delete("/api/admin/categories/{id}", category.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("카테고리 id")
                        ))
                );

        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).isEmpty();
    }

    @Nested
    @DisplayName("Category API 예외 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("Category Id 예외 - 올바르지 않은 Category Id")
        void invalidCategoryId() throws Exception {
            // given
            String newName = "newCategory";
            MockMultipartFile newIconFile = new MockMultipartFile(
                    "icon",
                    "newCategory.jpg",
                    "image/jpg",
                    "newCategory.jpg".getBytes());

            Long invalidCategoryId = 1L;

            // when
            ResultActions response =
                    mockMvc.perform(multipart("/api/admin/categories/{id}", invalidCategoryId)
                            .file(newIconFile)
                            .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8)))
                            .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

            // then
            response.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("error.status").value(INVALID_CATEGORY_ID.getStatus()))
                    .andExpect(jsonPath("error.code").value(INVALID_CATEGORY_ID.getCode()))
                    .andExpect(jsonPath("error.message").value(INVALID_CATEGORY_ID.getMessage()));
        }
    }
}