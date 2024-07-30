package com.givemecon.jwt;

import com.givemecon.common.auth.dto.OAuth2Attributes;
import com.givemecon.common.auth.util.OAuth2MemberUtils;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.givemecon.domain.entity.member.Role.*;
import static com.givemecon.common.auth.enums.OAuth2Provider.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@DataJpaTest
class OAuth2MemberUtilsTest {

    @Autowired
    MemberRepository memberRepository;

    OAuth2MemberUtils oauth2MemberUtils;

    OAuth2Attributes oauth2Attributes;

    @BeforeEach
    void setup() {
        oauth2MemberUtils = new OAuth2MemberUtils(memberRepository);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(GOOGLE.getEmailAttributeKey(), "tester@gmail.com");
        attributes.put(GOOGLE.getUsernameAttributeKey(), "tester");
        oauth2Attributes = OAuth2Attributes.of(GOOGLE.name(), attributes);
    }

    @Test
    @DisplayName("새로운 사용자가 OAuth2 로그인을 진행")
    void saveNewOAuth2Member() {
        Member newMember = oauth2MemberUtils.saveNewOrGetExisting(oauth2Attributes, GOOGLE.name());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).contains(newMember);
    }

    @Test
    @DisplayName("이미 이전에 같은 OAuth2 제공자를 통해 계정이 생성된 경우, 계정을 새로 생성하지 않음")
    void existingOAuth2Member() {
        Member member = memberRepository.save(oauth2Attributes.toEntity());
        Member newMember = oauth2MemberUtils.saveNewOrGetExisting(oauth2Attributes, GOOGLE.name());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).contains(newMember);
        assertThat(member).isEqualTo(newMember);
    }

    @Test
    @DisplayName("OAuth2 이메일과 같은 이메일이 사용된 계정이 존재하지만 해당 계정의 OAuth2 제공자가 다를 경우, 예외를 던짐.")
    void sameEmailButDifferentProvider() {
        memberRepository.save(Member.oauthBuilder()
                .email(oauth2Attributes.getEmail())
                .username(oauth2Attributes.getUsername())
                .role(USER)
                .provider(KAKAO)
                .build());

        assertThatThrownBy(() -> oauth2MemberUtils.saveNewOrGetExisting(oauth2Attributes, GOOGLE.name()))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }
}