package com.givemecon.application.service;

import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.common.exception.concrete.EntityNotFoundException;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.application.dto.MemberDto.*;
import static com.givemecon.domain.entity.member.Authority.ADMIN;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenService jwtTokenService;

    public SignupResponse signup(SignupRequest signupRequest) {
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
            throw new RuntimeException(); // TODO: 예외 처리
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        Member member = memberRepository.save(signupRequest.toEntity(encodedPassword));

        return new SignupResponse(member);
    }

    public TokenInfo login(LoginRequest loginRequest) {
        Member loginMember = memberRepository.findByEmail(loginRequest.getEmail())
                .filter(member -> passwordEncoder.matches(loginRequest.getPassword(), member.getPassword()))
                .filter(member -> member.getAuthority() == ADMIN)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        return jwtTokenService.getTokenInfo(new TokenRequest(loginMember));
    }

    public void delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        memberRepository.delete(member);
    }
}
