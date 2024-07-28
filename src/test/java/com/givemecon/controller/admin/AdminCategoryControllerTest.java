package com.givemecon.controller.admin;

import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import com.givemecon.infrastructure.s3.S3MockConfig;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(S3MockConfig.class)
@WithMockUser(roles = "ADMIN")
@Transactional
@SpringBootTest
class AdminCategoryControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryIconRepository categoryIconRepository;

    @Autowired
    S3Mock s3Mock;

    @Autowired
    S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

        s3Mock.start();
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(bucketName)
                .build());
    }

    @AfterEach
    void shutdown() {
        s3Mock.stop();
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
                .part(new MockPart("name", name.getBytes(StandardCharsets.UTF_8))));

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
                .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8))));

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
        ResultActions response = mockMvc.perform(delete("/api/admin/categories/{id}", category.getId()));

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
}