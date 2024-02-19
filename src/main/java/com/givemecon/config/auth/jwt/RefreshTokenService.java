package com.givemecon.config.auth.jwt;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import com.givemecon.util.exception.concrete.ExpiredTokenException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public String reissueAccessToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(RefreshToken.class));

        Member member = memberRepository.findById(tokenEntity.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        try {
            jwtTokenProvider.getClaims(tokenEntity.getRefreshToken());
        } catch (JwtException e) {
            refreshTokenRepository.delete(tokenEntity);
            throw new ExpiredTokenException(REFRESH_TOKEN_EXPIRED);
        }

        return jwtTokenProvider.generateAccessToken(member);
    }

    public void save(Long memberId, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(memberId, refreshToken));
    }
}
