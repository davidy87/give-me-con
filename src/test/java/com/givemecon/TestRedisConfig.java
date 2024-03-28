package com.givemecon;

import com.givemecon.config.auth.jwt.token.RefreshTokenRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@TestConfiguration
@EnableRedisRepositories(basePackageClasses = RefreshTokenRepository.class)
public class TestRedisConfig {
}
