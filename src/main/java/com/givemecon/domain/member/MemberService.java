package com.givemecon.domain.member;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.MemberDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

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
                .filter(member -> member.getRole() == Role.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return jwtTokenProvider.getTokenInfo(loginMember);
    }

    public Long delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        memberRepository.delete(member);

        return id;
    }

}
