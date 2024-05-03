package com.givemecon.domain.member;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.domain.member.MemberDto.*;

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

    public Long delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        memberRepository.delete(member);

        return id;
    }
}
