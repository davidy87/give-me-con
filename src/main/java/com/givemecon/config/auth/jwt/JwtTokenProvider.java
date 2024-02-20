package com.givemecon.config.auth.jwt;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.domain.member.Member;
import com.givemecon.util.exception.concrete.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.givemecon.util.error.ErrorCode.*;

@Slf4j
@Service
public class JwtTokenProvider {

    private final SecretKey key;

    private final RefreshTokenRepository refreshTokenRepository;

    private static final long ACCESS_TOKEN_DURATION = Duration.ofMinutes(30).toMillis(); // 30 mins

    private static final long REFRESH_TOKEN_DURATION = Duration.ofDays(14).toMillis(); // 14 days

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret,
                            RefreshTokenRepository refreshTokenRepository) {

        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * 로그인 성공 시, 응답으로 전달하는 토큰 정보
     * @param member 로그인 성공 후 생성된 사용자 entity
     * @return {@link TokenInfo} (Grant type, access token, refresh token이 담겨있는 DTO)
     */
    public TokenInfo getTokenInfo(Member member) {
        String accessToken = generateAccessToken(member);
        String refreshToken = generateRefreshToken();

        refreshTokenRepository.findByMemberId(member.getId())
                .ifPresentOrElse(
                        entity -> entity.setRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken))
                );

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(member.getRole())
                .build();
    }

    public String generateAccessToken(Member member) {
        String authorities = Stream.of(new SimpleGrantedAuthority(member.getRoleKey()))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(member.getUsername())
                .claim("auth", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_DURATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_DURATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * @param accessToken 사용자의 access token
     * @return {@link Authentication} claim에 담겨있는 정보를 바탕으로 만든 authentication token
     * @throws JwtException getClaims 호출 시, accessToken이 올바르지 않다면, JwtException을 던짐
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = getClaims(accessToken);
        Object auth = claims.get("auth");

        if (auth == null) {
            throw new InvalidTokenException(TOKEN_NOT_AUTHENTICATED);
        }

        log.info("--- In JwtTokenProvider ---");
        log.info("username = {}", claims.getSubject());

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(auth.toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 요청으로 전달된 토큰을 추출
     * @param request API 요청
     * @return Access token (만약 Authorization header가 없는 요청이거나 올바르지 않은 요청일 경우, <code>null</code>)
     */
    public String retrieveToken(HttpServletRequest request) {
        String grantType = "Bearer ";
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(grantType)) {
            return bearerToken.substring(grantType.length());
        }

        return null;
    }

    public Claims getClaims(String accessToken) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
