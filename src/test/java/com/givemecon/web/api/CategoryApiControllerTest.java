package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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

import static com.givemecon.web.dto.CategoryDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@WithMockUser(roles = "ADMIN")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CategoryApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .build();
    }

    @Test
    void save() throws Exception {
        // given
        String name = "coffee";
        String icon = "coffee.jpg";
        CategorySaveRequest requestDto = CategorySaveRequest.builder()
                .name(name)
                .icon(icon)
                .build();

        String url = "http://localhost:" + port + "/api/categories";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("저장할 카테고리 이름"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("저장할 카테고리 아이콘")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 카테고리 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("저장된 카테고리 이름"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("저장된 카테고리 아이콘")
                        ))
                );

        // then
        List<Category> categoryList = categoryRepository.findAll();

        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(categoryList.get(0).getId()))
                .andExpect(jsonPath("name").value(categoryList.get(0).getName()))
                .andExpect(jsonPath("icon").value(categoryList.get(0).getIcon()));
    }

    @Test
    void findAll() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            Category category = Category.builder()
                    .name("category" + i)
                    .icon("category" + i + ".png")
                    .build();

            categoryRepository.save(category);
        }

        String url = "http://localhost:" + port + "/api/categories";

        // when
        ResultActions response = mockMvc.perform(get(url))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("카테고리 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("[].icon").type(JsonFieldType.STRING).description("카테고리 아이콘")
                        ))
                );

        // then
        response
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        // given
        String name = "Bubble Tea";
        String icon = "bubble_tea.jpg";
        Category category = Category.builder()
                .name(name)
                .icon(icon)
                .build();

        Long id = categoryRepository.save(category).getId();
        String url = "http://localhost:" + port + "/api/categories/" + id;
        CategoryUpdateRequest requestDto = CategoryUpdateRequest.builder()
                .name("Smoothie")
                .icon("smoothie.jpg")
                .build();

        // when
        ResultActions response = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경할 카테고리 이름"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("변경할 카테고리 아이콘")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("변경된 카테고리 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 카테고리 이름"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("변경된 카테고리 아이콘")
                        ))
                );

        // then
        List<Category> categoryList = categoryRepository.findAll();

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(categoryList.get(0).getId()))
                .andExpect(jsonPath("name").value(categoryList.get(0).getName()))
                .andExpect(jsonPath("icon").value(categoryList.get(0).getIcon()));
    }

    @Test
    void deleteOne() throws Exception {
        // given
        String name = "Bubble Tea";
        String icon = "bubble_tea.jpg";
        Category category = Category.builder()
                .name(name)
                .icon(icon)
                .build();

        Long id = categoryRepository.save(category).getId();
        String url = "http://localhost:" + port + "/api/categories/" + id;

        // when
        ResultActions response = mockMvc.perform(delete(url))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}"));

        // then
        response.andExpect(status().isNoContent());

        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).isEmpty();
    }
}