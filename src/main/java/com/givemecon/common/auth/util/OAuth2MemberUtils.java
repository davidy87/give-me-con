package com.givemecon.common.auth.util;

import com.givemecon.common.auth.CustomOAuth2UserService;
import com.givemecon.common.auth.dto.OAuth2Attributes;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OAuth2MemberUtils {

    private static final String DUPLICATE_EMAIL_ERROR_CODE = "duplicate-email";

    private final MemberRepository memberRepository;

    /**
     * {@link CustomOAuth2UserService}에서 사용하는 method. {@link OAuth2Attributes}에 담긴 email로 {@link Member} entity를
     * 찾는다. 해당하는 entity의 OAuth2 provider가 파라미터로 전달된 registrationId와 다를 경우,
     * {@link OAuth2AuthenticationException}을 던진다. 만약 해당하는 entity를 찾지 못한 경우, 새로운 entity를 생성해
     * {@link MemberRepository}에 저장한다.
     *
     * @param attributes {@link OAuth2Attributes} 클래스로, OAuth2 로그인 시도 후 전달되는 사용사의 email, username,
     *                                            OAuth2 provider 등의 정보가 담긴 객체
     * @param registrationId OAuth2의 제공자 id. 모두 대문자로 된 상태로 전달된다(e.g. Google OAuth2 -> GOOGLE).
     * @return {@link Member} - 기존에 있던 혹은 새로 저장된 Member entity
     * @throws OAuth2AuthenticationException 이미 생성된 Member가 존재할 경우
     */
    @Transactional
    public Member saveNewOrGetExisting(OAuth2Attributes attributes, String registrationId) throws OAuth2AuthenticationException {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .filter(entity -> checkMemberExistence(entity, registrationId))
                .orElse(attributes.toEntity());

        return memberRepository.save(member);
    }

    /**
     * <code>saveNewOrGetExisting()</code>에서만 사용되는 helper method. 매개변수로 전달된 {@link Member} entity의 provider가 null인지
     * 확인하고, registrationId와 같은지 확인한다. 만약 같지 않다면, 이미 존재하는 사용자임을 나타내기 위해
     * {@link OAuth2AuthenticationException}을 던진다.
     *
     * @param member {@link Member} entity 객체
     * @param registrationId OAuth2의 제공자 id. 모두 대문자로 된 상태로 전달된다(e.g. Google OAuth2 -> GOOGLE).
     * @return member의 provider와 registrationId가 같을 경우 <code>true</code>
     * @throws OAuth2AuthenticationException member의 provider와 registrationId가 같지 않을 경우 던진다.
     */
    private boolean checkMemberExistence(Member member, String registrationId) {
        return Optional.ofNullable(member.getProvider())
                .filter(provider -> provider.name().equals(registrationId))
                .map(provider -> true)
                .orElseThrow(() -> new OAuth2AuthenticationException(DUPLICATE_EMAIL_ERROR_CODE));
    }
}
