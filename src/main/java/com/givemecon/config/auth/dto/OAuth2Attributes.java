package com.givemecon.config.auth.dto;

import com.givemecon.config.auth.OAuth2Provider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

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

    public static OAuth2Attributes of(String usernameAttributeName, Map<String, Object> attributes) {
        return ofGoogle(usernameAttributeName, attributes);
    }

    private static OAuth2Attributes ofGoogle(String usernameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .attributes(attributes)
                .nameAttributeKey(usernameAttributeName)
                .username((String) attributes.get("given_name"))
                .email((String) attributes.get("email"))
                .provider(OAuth2Provider.GOOGLE)
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
