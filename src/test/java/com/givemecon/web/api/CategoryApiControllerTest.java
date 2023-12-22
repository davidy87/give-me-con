package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.givemecon.web.dto.CategoryDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
@WithMockUser(roles = "ADMIN")
class CategoryApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void saveCategory() throws Exception {
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
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // then
        List<Category> categoryList = categoryRepository.findAll();

        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(categoryList.get(0).getId()))
                .andExpect(jsonPath("name").value(categoryList.get(0).getName()))
                .andExpect(jsonPath("icon").value(categoryList.get(0).getIcon()));
    }

    @Test
    void findAllCategories() throws Exception {
        // given
        for (int i = 1; i <= 10; i++) {
            Category category = Category.builder()
                    .name("category" + i)
                    .icon("category" + i + ".png")
                    .build();

            categoryRepository.save(category);
        }

        String url = "http://localhost:" + port + "/api/categories";

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response
                .andExpect(status().isOk());
    }

    @Test
    void updateCategory() throws Exception {
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
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // then
        List<Category> categoryList = categoryRepository.findAll();

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(categoryList.get(0).getId()))
                .andExpect(jsonPath("name").value(categoryList.get(0).getName()))
                .andExpect(jsonPath("icon").value(categoryList.get(0).getIcon()));
    }

    @Test
    void deleteCategory() throws Exception {
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
        ResultActions response = mockMvc.perform(delete(url));

        // then
        response.andExpect(status().isOk());

        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).isEmpty();
    }
}