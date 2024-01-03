package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
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

import static com.givemecon.web.ApiDocumentUtils.*;
import static com.givemecon.web.dto.BrandDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@WithMockUser(roles = "ADMIN")
@SpringBootTest
class BrandApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

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
        String name = "Starbucks";
        String icon = "starbucks.jpg";
        BrandSaveRequest requestDto = BrandSaveRequest.builder()
                .name(name)
                .icon(icon)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // then
        List<Brand> brandList = brandRepository.findAll();

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(brandList.get(0).getId()))
                .andExpect(jsonPath("name").value(brandList.get(0).getName()))
                .andExpect(jsonPath("icon").value(brandList.get(0).getIcon()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("저장할 브랜드 이름"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("저장할 브랜드 아이콘")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 브랜드 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("저장된 브랜드 이름"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("저장된 브랜드 아이콘")
                        ))
                );
    }

    @Test
    void findAll() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            Brand brand = Brand.builder()
                    .name("brand" + i)
                    .icon("brand" + i + ".png")
                    .build();

            brandRepository.save(brand);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/brands"));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("브랜드 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("브랜드명"),
                                fieldWithPath("[].icon").type(JsonFieldType.STRING).description("브랜드 아이콘")
                        ))
                );
    }

    @Test
    void findAllByCategoryId() throws Exception {
        // given
        String name = "Bubble Tea";
        String icon = "bubble_tea.jpg";
        Category category = Category.builder()
                .name(name)
                .icon(icon)
                .build();

        Category categorySaved = categoryRepository.save(category);

        for (int i = 1; i <= 5; i++) {
            Brand brand = Brand.builder()
                    .icon("brand_" + i + ".png")
                    .name("Brand " + i)
                    .build();

            Brand brandSaved = brandRepository.save(brand);
            categorySaved.addBrand(brandSaved);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/brands?categoryId={categoryId}", categorySaved.getId()));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("브랜드 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("브랜드명"),
                                fieldWithPath("[].icon").type(JsonFieldType.STRING).description("브랜드 아이콘")
                        ))
                );

        List<Brand> brandList = brandRepository.findAll();

        for (Brand brand : brandList) {
            assertThat(brand.getCategory()).isEqualTo(categorySaved);
        }
    }

    @Test
    void update() throws Exception {
        // given
        String name = "Paris Baguette";
        String icon = "paris_baguette.jpg";
        Brand brand = Brand.builder()
                .name(name)
                .icon(icon)
                .build();

        Long id = brandRepository.save(brand).getId();
        BrandUpdateRequest requestDto = BrandUpdateRequest.builder()
                .name("Tous Res Jours")
                .icon("tous_res_jours.jpg")
                .build();

        // when
        ResultActions response = mockMvc.perform(put("/api/brands/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("브랜드 id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).optional().description("변경할 브랜드명"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).optional().description("변경할 브랜드 아이콘")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("변경된 브랜드 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 브랜드명"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("변경된 브랜드 아이콘")
                        ))
                );

        // then
        List<Brand> brandList = brandRepository.findAll();

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(brandList.get(0).getId()))
                .andExpect(jsonPath("name").value(brandList.get(0).getName()))
                .andExpect(jsonPath("icon").value(brandList.get(0).getIcon()));
    }

    @Test
    void deleteOne() throws Exception {
        // given
        String name = "Starbucks";
        String icon = "starbucks.jpg";
        Brand brand = Brand.builder()
                .name(name)
                .icon(icon)
                .build();

        Long id = brandRepository.save(brand).getId();

        // when
        ResultActions response = mockMvc.perform(delete("/api/brands/{id}", id));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("브랜드 id")
                        ))
                );

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).isEmpty();
    }
}