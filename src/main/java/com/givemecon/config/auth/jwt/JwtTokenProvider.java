package com.givemecon.config.auth.jwt;

import com.givemecon.config.auth.dto.TokenInfo;
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

@Slf4j
@Service
public class JwtTokenProvider {

    private final SecretKey key;

    private static final long ACCESS_TOKEN_DURATION = Duration.ofMinutes(30).toMillis(); // 30 mins

    private static final long REFRESH_TOKEN_DURATION = Duration.ofDays(14).toMillis(); // 14 days

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret) {
        if (jwtSecret.equals("test")) {
            this.key = null;
        } else {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        }
    }

    public TokenInfo generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_DURATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_DURATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보 추출
    public Authentication getAuthentication(String accessToken) throws JwtException {
        Claims claims = getClaims(accessToken);
        Object auth = claims.get("auth");

        if (auth == null) {
            throw new RuntimeException("Unauthorized Token"); // TODO: 예외 처리
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

    public String retrieveToken(HttpServletRequest request) {
        String grantType = "Bearer ";
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(grantType)) {
            return bearerToken.substring(grantType.length());
        }

        return null;
    }

    private Claims getClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
