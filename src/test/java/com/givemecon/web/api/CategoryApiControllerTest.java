package com.givemecon.web.api;

import com.givemecon.S3MockConfig;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryIcon;
import com.givemecon.domain.category.CategoryIconRepository;
import com.givemecon.domain.category.CategoryRepository;
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

import static com.givemecon.web.ApiDocumentUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(S3MockConfig.class)
@Transactional
@WithMockUser(roles = "ADMIN")
@SpringBootTest
class CategoryApiControllerTest {

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
        String name = "icon";
        String icon = "icon.jpg";
        MockMultipartFile iconFile = new MockMultipartFile(name, icon, "image/jpg", icon.getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/categories")
                .file(iconFile)
                .part(new MockPart("name", name.getBytes(StandardCharsets.UTF_8))));

        // then
        List<Category> categoryList = categoryRepository.findAll();

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(categoryList.get(0).getId()))
                .andExpect(jsonPath("name").value(categoryList.get(0).getName()))
                .andExpect(jsonPath("icon").value(categoryList.get(0).getCategoryIcon().getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("name").description("저장할 카테고리 이름"),
                                partWithName("icon").description("저장할 카테고리 아이콘 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 카테고리 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("저장된 카테고리 이름"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("저장된 카테고리 아이콘 URL")
                        ))
                );
    }

    @Test
    void findAll() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            Category category = categoryRepository.save(Category.builder()
                    .name("category" + i)
                    .build());

            CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("categoryIcon")
                    .build());

            category.setCategoryIcon(categoryIcon);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/categories"));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("카테고리 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("[].icon").type(JsonFieldType.STRING).description("카테고리 아이콘")
                        ))
                );
    }

    @Test
    void update() throws Exception {
        // given
        String name = "category";
        String icon = "category.jpg";
        String imageKey = "imageKey";
        String imageUrl = "imageUrl";

        Category category = categoryRepository.save(Category.builder()
                .name(name)
                .build());

        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey(imageKey)
                .originalName(icon)
                .imageUrl(imageUrl)
                .build());

        category.setCategoryIcon(categoryIcon);

        String newName = "newCategory";
        MockMultipartFile newIconFile = new MockMultipartFile(
                "icon",
                "newCategory.jpg",
                "image/jpg",
                "newCategory.jpg".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/categories/{id}", category.getId())
                .file(newIconFile)
                .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8))));

        // then
        List<Category> categoryList = categoryRepository.findAll();

        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(categoryList.get(0).getId()))
                .andExpect(jsonPath("name").value(categoryList.get(0).getName()))
                .andExpect(jsonPath("icon").value(categoryList.get(0).getCategoryIcon().getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("카테고리 id")
                        ),
                        requestParts(
                                partWithName("name").description("수정할 카테고리 이름").optional(),
                                partWithName("icon").description("수정할 카테고리 아이콘 파일").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("변경된 카테고리 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 카테고리 이름"),
                                fieldWithPath("icon").type(JsonFieldType.VARIES).description("변경된 카테고리 아이콘")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        String name = "Bubble Tea";
        String icon = "bubble_tea.jpg";
        Category category = Category.builder()
                .name(name)
                .build();

        Long id = categoryRepository.save(category).getId();

        // when
        ResultActions response = mockMvc.perform(delete("/api/categories/{id}", id));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("카테고리 id")
                        ))
                );

        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).isEmpty();
    }
}