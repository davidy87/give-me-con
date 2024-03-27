package com.givemecon.config;

import com.givemecon.config.auth.jwt.token.RefreshTokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackageClasses = RefreshTokenRepository.class)
public class RedisConfig {
}
