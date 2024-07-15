package com.givemecon.config.auth;

import com.givemecon.config.auth.dto.OAuth2Attributes;
import com.givemecon.config.auth.util.OAuth2MemberUtils;
import com.givemecon.domain.entity.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collections;

@RequiredArgsConstructor
@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2MemberUtils oauth2MemberUtils;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, oAuth2User.getAttributes());
        Member member = oauth2MemberUtils.saveNewOrGetExisting(attributes, registrationId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }
}
