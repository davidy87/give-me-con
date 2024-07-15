package com.givemecon.common.configuration;

import com.givemecon.common.auth.jwt.token.RefreshTokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackageClasses = RefreshTokenRepository.class)
public class RedisConfig {
}
