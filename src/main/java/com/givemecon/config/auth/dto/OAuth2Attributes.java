package com.givemecon.config.auth.dto;

import com.givemecon.config.auth.OAuth2Provider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.givemecon.config.auth.NameAttributeKey.*;
import static com.givemecon.config.auth.OAuth2Provider.*;

@Slf4j
@Getter
public class OAuth2Attributes {

    private final Map<String, Object> attributes;

    private final String nameAttributeKey;

    private final String username;

    private final String email;

    private final OAuth2Provider provider;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String username, String email, OAuth2Provider provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.provider = provider;
    }

    public static OAuth2Attributes of(String registrationId, Map<String, Object> attributes) {
        OAuth2Provider oAuth2Provider = OAuth2Provider.valueOf(registrationId);
        return switch (oAuth2Provider) {
            case GOOGLE -> ofGoogle(GOOGLE_USERNAME_KEY.getKey(), attributes);
            case NAVER -> ofNaver(NAVER_USERNAME_KEY.getKey(), attributes);
            case KAKAO -> ofKakao(KAKAO_USERNAME_KEY.getKey(), attributes);
        };
    }

    private static OAuth2Attributes ofGoogle(String userNameAttributeKey, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeKey)
                .username((String) attributes.get(userNameAttributeKey))
                .email((String) attributes.get("email"))
                .provider(GOOGLE)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofNaver(String userNameAttributeKey, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attributes.builder()
                .attributes(response)
                .nameAttributeKey(userNameAttributeKey)
                .username((String) response.get(userNameAttributeKey))
                .email((String) response.get("email"))
                .provider(NAVER)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(String userNameAttributeKey, Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuth2Attributes.builder()
                .attributes(properties)
                .nameAttributeKey(userNameAttributeKey)
                .username((String) properties.get(userNameAttributeKey))
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
