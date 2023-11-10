package com.givemecon.web.api;

import com.givemecon.util.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
@WithMockUser(roles = "ADMIN")
public class ApiExceptionControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void categoryExceptionTest() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/categories/" + 1;

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("status").value(ErrorCode.NOT_FOUND.name()))
                .andExpect(jsonPath("code").value(ErrorCode.NOT_FOUND.getCode()))
                .andExpect(jsonPath("message").value(ErrorCode.NOT_FOUND.getMessage()));
    }

    @Test
    void brandsExceptionTest() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/brands/" + 1;

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("status").value(ErrorCode.NOT_FOUND.name()))
                .andExpect(jsonPath("code").value(ErrorCode.NOT_FOUND.getCode()))
                .andExpect(jsonPath("message").value(ErrorCode.NOT_FOUND.getMessage()));
    }

    @Test
    void voucherExceptionTest() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/vouchers/" + 1;

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("status").value(ErrorCode.NOT_FOUND.name()))
                .andExpect(jsonPath("code").value(ErrorCode.NOT_FOUND.getCode()))
                .andExpect(jsonPath("message").value(ErrorCode.NOT_FOUND.getMessage()));
    }

    @Test
    void memberExceptionTest() throws Exception {
        // given
        String url = "http://localhost:" + port + "/api/members/" + 1;

        // when
        ResultActions response = mockMvc.perform(delete(url));

        // then
        response
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("status").value(ErrorCode.NOT_FOUND.name()))
                .andExpect(jsonPath("code").value(ErrorCode.NOT_FOUND.getCode()))
                .andExpect(jsonPath("message").value(ErrorCode.NOT_FOUND.getMessage()));
    }
}
