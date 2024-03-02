package com.givemecon.config.auth;

import com.givemecon.config.auth.dto.OAuth2Attributes;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@RequiredArgsConstructor
@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, oAuth2User.getAttributes());
        Member member = saveNewOrUpdate(attributes, registrationId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private Member saveNewOrUpdate(OAuth2Attributes attributes, String registrationId) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .filter(entity -> checkMemberExistence(entity, registrationId))
                .map(entity -> entity.update(attributes.getEmail(), attributes.getUsername()))
                .orElse(attributes.toEntity());

        return memberRepository.save(member);
    }

    private boolean checkMemberExistence(Member member, String registrationId) {
        if (member == null) {
            return false;
        } else if (member.getProvider().name().equals(registrationId)) {
            return true;
        }

        throw new OAuth2AuthenticationException("이미 해당 이메일로 가입된 계정이 존재합니다.");
    }
}