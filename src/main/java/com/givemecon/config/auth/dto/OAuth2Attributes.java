package com.givemecon.config.auth.dto;

import com.givemecon.config.enums.OAuth2Provider;
import com.givemecon.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

import static com.givemecon.config.enums.Authority.*;

@Getter
@Builder
public final class OAuth2Attributes {

    private final Map<String, Object> attributes;

    private final String nameAttributeKey;

    private final String username;

    private final String email;

    private final OAuth2Provider provider;

    public static OAuth2Attributes of(String registrationId, Map<String, Object> attributes) {
        OAuth2Provider oAuth2Provider = OAuth2Provider.valueOf(registrationId.toUpperCase());

        return switch (oAuth2Provider) {
            case GOOGLE -> ofGoogle(oAuth2Provider, attributes);
            case NAVER -> ofNaver(oAuth2Provider, attributes);
            case KAKAO -> ofKakao(oAuth2Provider, attributes);
        };
    }

    private static OAuth2Attributes ofGoogle(OAuth2Provider provider, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .attributes(attributes)
                .nameAttributeKey(provider.getUsernameAttributeKey())
                .username((String) attributes.get(provider.getUsernameAttributeKey()))
                .email((String) attributes.get(provider.getEmailAttributeKey()))
                .provider(provider)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofNaver(OAuth2Provider provider, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attributes.builder()
                .attributes(response)
                .nameAttributeKey(provider.getUsernameAttributeKey())
                .username((String) response.get(provider.getUsernameAttributeKey()))
                .email((String) response.get(provider.getEmailAttributeKey()))
                .provider(provider)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(OAuth2Provider provider, Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuth2Attributes.builder()
                .attributes(properties)
                .nameAttributeKey(provider.getUsernameAttributeKey())
                .username((String) properties.get(provider.getUsernameAttributeKey()))
                .email((String) kakaoAccount.get(provider.getEmailAttributeKey()))
                .provider(provider)
                .build();
    }

    public Member toEntity() {
        return Member.oauthBuilder()
                .email(email)
                .username(username)
                .authority(USER)
                .provider(provider)
                .build();
    }
}
