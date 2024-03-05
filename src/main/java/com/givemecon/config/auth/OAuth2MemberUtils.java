package com.givemecon.config.auth;

import com.givemecon.config.auth.dto.OAuth2Attributes;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OAuth2MemberUtils {

    private final MemberRepository memberRepository;

    /**
     * {@link CustomOAuth2UserService}에서 사용하는 method. {@link OAuth2Attributes}에 담긴 email로
     * {@link Member} entity를 찾는다. 해당하는 entity의 OAuth2 provider가 파라미터로 전달된 registrationId와 다를 경우,
     * {@link OAuth2AuthenticationException}을 던진다. 그렇지 않은 경우에는 해당하는 entity의 email과 username을 OAuth2Attributes에
     * 담긴 email과 username로 변경한다. 만약 해당하는 entity를 찾지 못한 경우, 새로운 entity를 생성해 {@link MemberRepository}에 저장한다.
     *
     * @param attributes {@link OAuth2Attributes} 클래스로, OAuth2 로그인 시도 후 전달되는 사용사의 email, username, OAuth2
     *                                           provider 등의 정보가 담긴 객체
     * @param registrationId OAuth2의 제공자 id. 모두 대문자로 된 상태로 전달된다(e.g. Google OAuth2 -> GOOGLE).
     * @return {@link Member} - 새로 생성되거나 변경된 Member entity를 반환한다.
     * @throws OAuth2AuthenticationException 이미 생성된 Member가 존재할 경우 던진다.
     */
    public Member saveNewOrUpdate(OAuth2Attributes attributes, String registrationId) throws OAuth2AuthenticationException {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .filter(entity -> checkMemberExistence(entity, registrationId))
                .map(entity -> entity.update(attributes.getEmail(), attributes.getUsername()))
                .orElse(attributes.toEntity());

        return memberRepository.save(member);
    }

    /**
     * <code>saveNewOrUpdate()</code>에서만 사용되는 helper method. 매개변수로 전달된 {@link Member} entity가 null인지, 만약 null이
     * 아니라면 이 entity의 OAuth2 provider가 매개변수로 넘어온 registrationId와 같은지 확인한다. 만약 해당하지 않다면, 이미 존재하는 사용자임을
     * 나타내기 위해 {@link OAuth2AuthenticationException}을 던진다.
     *
     * @param member {@link Member} entity 객체
     * @param registrationId OAuth2의 제공자 id. 모두 대문자로 된 상태로 전달된다(e.g. Google OAuth2 -> GOOGLE).
     * @return member가 null일 경우 <code>false</code>,
     *         member의 provider와 registrationId가 같을 경우 <code>true</code>
     * @throws OAuth2AuthenticationException member의 provider와 registrationId가 같지 않을 경우 던진다.
     */
    private boolean checkMemberExistence(Member member, String registrationId) {
        if (member == null) {
            return false;
        } else if (member.getProvider().name().equals(registrationId)) {
            return true;
        }

        throw new OAuth2AuthenticationException("이미 해당 이메일로 가입된 계정이 존재합니다.");
    }
}
