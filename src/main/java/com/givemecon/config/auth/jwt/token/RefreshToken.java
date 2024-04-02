package com.givemecon.config.auth.jwt.token;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import static com.givemecon.config.enums.TokenDuration.REFRESH_TOKEN_DURATION;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refresh_token")
public class RefreshToken {

    @Id
    private String id;

    @Indexed
    private String memberId;

    @Indexed
    private String refreshToken;

    @TimeToLive
    private Long expiration;

    @Builder
    public RefreshToken(String memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
        this.expiration = REFRESH_TOKEN_DURATION.duration();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        this.expiration = REFRESH_TOKEN_DURATION.duration();
    }
}
