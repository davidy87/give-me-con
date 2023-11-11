package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
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

import static com.givemecon.web.dto.BrandDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
@WithMockUser(roles = "ADMIN")
class BrandApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    BrandRepository brandRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void saveBrand() throws Exception {
        // given
        String name = "Starbucks";
        String icon = "starbucks.jpg";
        BrandSaveRequest requestDto = BrandSaveRequest.builder()
                .name(name)
                .icon(icon)
                .build();

        String url = "http://localhost:" + port + "/api/brands";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // then
        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("icon").value(icon));

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList.get(0).getName()).isEqualTo(name);
        assertThat(brandList.get(0).getIcon()).isEqualTo(icon);
    }

    @Test
    void findAllBrands() throws Exception {
        // given
        for (int i = 1; i <= 10; i++) {
            Brand brand = Brand.builder()
                    .name("brand" + i)
                    .icon("brand" + i + ".png")
                    .build();

            brandRepository.save(brand);
        }

        String url = "http://localhost:8080" + port + "/api/brands";

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response.andExpect(status().isOk());
    }

    @Test
    void findBrand() throws Exception {
        // given
        String name = "Starbucks";
        String icon = "starbucks.jpg";
        Brand brand = Brand.builder()
                .name(name)
                .icon(icon)
                .build();

        Long id = brandRepository.save(brand).getId();
        String url = "http://localhost:" + port + "/api/brands/" + id;

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("icon").value(icon));
    }

    @Test
    void updateBrand() throws Exception {
        // given
        String name = "Paris Baguette";
        String icon = "paris_baguette.jpg";
        Brand brand = Brand.builder()
                .name(name)
                .icon(icon)
                .build();

        Long id = brandRepository.save(brand).getId();

        String url = "http://localhost:" + port + "/api/brands/" + id;
        BrandUpdateRequest requestDto = BrandUpdateRequest.builder()
                .name("Tous Res Jours")
                .icon("tous_res_jours.jpg")
                .build();

        // when
        ResultActions response = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // then
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(requestDto.getName()))
                .andExpect(jsonPath("icon").value(requestDto.getIcon()));

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList.get(0).getName()).isEqualTo(requestDto.getName());
        assertThat(brandList.get(0).getIcon()).isEqualTo(requestDto.getIcon());
    }

    @Test
    void deleteBrand() throws Exception {
        // given
        String name = "Starbucks";
        String icon = "starbucks.jpg";
        Brand brand = Brand.builder()
                .name(name)
                .icon(icon)
                .build();

        Long id = brandRepository.save(brand).getId();
        String url = "http://localhost:" + port + "/api/brands/" + id;

        // when
        ResultActions response = mockMvc.perform(delete(url));

        // then
        response.andExpect(status().isOk());

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).isEmpty();
    }
}