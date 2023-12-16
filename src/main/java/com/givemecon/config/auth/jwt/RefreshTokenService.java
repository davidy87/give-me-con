package com.givemecon.config.auth.jwt;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public String reissueAccessToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_EXPIRED));

        Member member = memberRepository.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND));

        try {
            jwtTokenProvider.getClaims(tokenEntity.getRefreshToken());
        } catch (Exception e) {
            refreshTokenRepository.delete(tokenEntity);
            throw new EntityNotFoundException(ErrorCode.TOKEN_EXPIRED);
        }

        return jwtTokenProvider.generateAccessToken(member);
    }

    public void save(Long memberId, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(memberId, refreshToken));
    }
}
