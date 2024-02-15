package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.givemecon.web.ApiDocumentUtils.*;
import static com.givemecon.domain.member.MemberDto.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
class MemberApiControllerTest {

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
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();
    }

    @Test
    void signup() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@gmail.com")
                .username("tester")
                .password("testpass")
                .passwordConfirm("testpass")
                .build();

        // when
        ResultActions response = mockMvc.perform(post("/api/members/admin/signup")
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
    void login() throws Exception {
        // given
        String password = "testpass";

        Member member = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build());

        LoginRequest loginRequest = LoginRequest.builder()
                .email(member.getEmail())
                .password(password)
                .build();

        // when
        ResultActions response = mockMvc.perform(post("/api/members/admin/login")
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
                                fieldWithPath("role").type(JsonFieldType.STRING).description("권한 타입")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        Member member = Member.builder()
                .username("tester")
                .email("test@gmail.com")
                .role(Role.USER)
                .build();

        Member memberSaved = memberRepository.save(member);

        // when
        ResultActions response = mockMvc.perform(delete("/api/members/{id}", memberSaved.getId()));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("회원 id")
                        ))
                );
    }
}