package com.givemecon.application.service;

import com.givemecon.application.exception.InvalidRequestFieldException;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.application.dto.MemberDto.*;
import static com.givemecon.application.exception.errorcode.MemberErrorCode.*;
import static com.givemecon.domain.entity.member.Role.ADMIN;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenService jwtTokenService;

    public SignupResponse signup(SignupRequest signupRequest) {
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
            throw new InvalidRequestFieldException(PASSWORD_NOT_MATCH);
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        Member member = memberRepository.save(signupRequest.toEntity(encodedPassword));

        return new SignupResponse(member);
    }

    public TokenInfo adminLogin(LoginRequest loginRequest) {
        Member loginMember = memberRepository.findByEmail(loginRequest.getEmail())
                .filter(member -> passwordEncoder.matches(loginRequest.getPassword(), member.getPassword()))
                .orElseThrow(() -> new InvalidRequestFieldException(PASSWORD_NOT_MATCH));

        if (loginMember.getRole() != ADMIN) {
            throw new InvalidRequestFieldException(ROLE_NOT_ADMIN);
        }

        return jwtTokenService.getTokenInfo(new TokenRequest(loginMember));
    }

    public void delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_MEMBER_ID));

        memberRepository.delete(member);
    }
}
