package com.givemecon.common.configuration;

import com.givemecon.common.auth.jwt.token.RefreshTokenRepository;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackageClasses = RefreshTokenRepository.class)
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisProperties redisProperties) {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory(redisProperties));
        return redisTemplate;
    }
}
