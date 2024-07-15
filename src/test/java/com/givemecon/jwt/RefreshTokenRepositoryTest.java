package com.givemecon.jwt;

import com.givemecon.common.auth.jwt.token.RefreshToken;
import com.givemecon.common.auth.jwt.token.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.givemecon.common.auth.enums.TokenDuration.REFRESH_TOKEN_DURATION;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("Redis에 RefreshToken 저장 후 여러 방법으로 조회")
    void saveAndFind() {
        // given
        RefreshToken refreshToken = new RefreshToken("1L", "refreshToken");

        // when
        refreshTokenRepository.save(refreshToken);
        Optional<RefreshToken> foundByMemberId = refreshTokenRepository.findByMemberId(refreshToken.getMemberId());
        Optional<RefreshToken> foundByRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken.getRefreshToken());

        // then
        assertThat(foundByMemberId).isNotEmpty();
        assertThat(foundByRefreshToken).isNotEmpty();
        assertThat(foundByMemberId.get().getMemberId()).isEqualTo(foundByRefreshToken.get().getMemberId());
        assertThat(foundByMemberId.get().getRefreshToken()).isEqualTo(foundByRefreshToken.get().getRefreshToken());
    }

    @Test
    @DisplayName("Redis에 저장된 RefreshToken 유효기간 확인")
    void saveAndCheckExpiration() {
        // given
        RefreshToken refreshToken = new RefreshToken("1L", "refreshToken");

        // when
        refreshTokenRepository.save(refreshToken);
        Optional<RefreshToken> found = refreshTokenRepository.findByMemberId(refreshToken.getMemberId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get().getExpiration()).isLessThanOrEqualTo(REFRESH_TOKEN_DURATION.toMillis());
    }
}
