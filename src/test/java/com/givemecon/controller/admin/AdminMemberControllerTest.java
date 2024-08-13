package com.givemecon.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.application.dto.MemberDto;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.givemecon.domain.entity.member.Role.ADMIN;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequest;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@Transactional
@SpringBootTest
class AdminMemberControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

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
    @DisplayName("Admin용 회원가입 요청 API 테스트")
    void signup() throws Exception {
        // given
        MemberDto.SignupRequest signupRequest = MemberDto.SignupRequest.builder()
                .email("test@gmail.com")
                .username("tester")
                .password("testpass")
                .passwordConfirm("testpass")
                .build();

        // when
        ResultActions response = mockMvc.perform(post("/api/admin/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signupRequest)));

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("username").value(signupRequest.getUsername()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("passwordConfirm").type(JsonFieldType.STRING).description("비밀번호 재입력")
                        ),
                        responseFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("회원가입된 회원 닉네임")
                        ))
                );
    }

    @Test
    @DisplayName("Admin용 로그인 요청 API 테스트")
    void login() throws Exception {
        // given
        String password = "testpass";

        Member member = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .password(passwordEncoder.encode(password))
                .role(ADMIN)
                .build());

        MemberDto.LoginRequest loginRequest = MemberDto.LoginRequest.builder()
                .email(member.getEmail())
                .password(password)
                .build();

        // when
        ResultActions response = mockMvc.perform(post("/api/admin/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequest)));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("grantType").type(JsonFieldType.STRING).description("인증 타입"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("Access Token"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("Refresh Token"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 닉네임"),
                                fieldWithPath("role").type(JsonFieldType.STRING).description("권한")
                        ))
                );
    }
}