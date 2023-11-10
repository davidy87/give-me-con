package com.givemecon.config.auth.dto;

import com.givemecon.config.auth.OAuth2Provider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.givemecon.config.auth.OAuth2Provider.*;

@Slf4j
@Getter
public class OAuth2Attributes {

    private Map<String, Object> attributes;

    private String nameAttributeKey;

    private String username;

    private String email;

    private OAuth2Provider provider;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String username, String email, OAuth2Provider provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.provider = provider;
    }

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        registrationId = registrationId.toUpperCase();

        if (registrationId.equals(NAVER.name())) {
            return ofNaver(userNameAttributeName, attributes);
        } else if (registrationId.equals(KAKAO.name())) {
            return ofKakao(userNameAttributeName, attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .username((String) attributes.get("given_name"))
                .email((String) attributes.get("email"))
                .provider(GOOGLE)
                .build();
    }

    private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get(userNameAttributeName);

        return OAuth2Attributes.builder()
                .attributes(response)
                .nameAttributeKey("id")
                .username((String) response.get("nickname"))
                .email((String) response.get("email"))
                .provider(NAVER)
                .build();
    }

    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuth2Attributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .username((String) properties.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .provider(KAKAO)
                .build();
    }

    public Member toEntity() {
        return Member.oauthBuilder()
                .email(email)
                .username(username)
                .role(Role.USER)
                .provider(provider)
                .build();
    }
}
