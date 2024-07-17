package com.givemecon.controller;

import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.brand.BrandIconRepository;
import com.givemecon.domain.repository.brand.BrandRepository;
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
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.givemecon.util.ApiDocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(S3MockConfig.class)
@WithMockUser(roles = "ADMIN")
@Transactional
@SpringBootTest
class BrandControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryIconRepository categoryIconRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    BrandIconRepository brandIconRepository;

    @Autowired
    S3Mock s3Mock;

    @Autowired
    S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    Category category;

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

        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("categoryIcon")
                .build());

        category = categoryRepository.save(Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build());
    }

    @AfterEach
    void stop() {
        s3Mock.stop();
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
        ResultActions result = mockMvc.perform(multipart("/api/brands")
                .file(iconFile)
                .part(new MockPart("categoryId", String.valueOf(category.getId()).getBytes(StandardCharsets.UTF_8)))
                .part(new MockPart("name", name.getBytes(StandardCharsets.UTF_8)))
        );

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
    void findAllByCategoryId() throws Exception {
        // given
        for (int i = 1; i <= 20; i++) {
            BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("brandIcon" + i + ".jpg")
                    .build());

            brandRepository.save(Brand.builder()
                    .name("Brand " + i)
                    .brandIcon(brandIcon)
                    .category(category)
                    .build());
        }

        // when
        String uri = UriComponentsBuilder.fromPath("/api/brands")
                .queryParam("categoryId", category.getId())
                .build()
                .toString();

        ResultActions response = mockMvc.perform(get(uri));

        // then
        List<Brand> brandList = brandRepository.findAll();

        for (Brand brand : brandList) {
            assertThat(brand.getCategory()).isEqualTo(category);
        }

        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pagingQueryParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("페이징된 브랜드 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("페이징된 브랜드 name"),
                                fieldWithPath("[].iconUrl").type(JsonFieldType.STRING).description("페이징된 브랜드 iconUrl")
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
        ResultActions response = mockMvc.perform(multipart("/api/brands/{id}", brand.getId())
                .file(newIconFile)
                .part(new MockPart("categoryId", newCategory.getId().toString().getBytes()))
                .part(new MockPart("name", newName.getBytes()))
        );

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
        ResultActions response = mockMvc.perform(delete("/api/brands/{id}", brand.getId()));

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
}