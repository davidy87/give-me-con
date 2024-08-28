package com.givemecon.common.configuration;

import com.givemecon.common.util.PortUtils;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Profile({"dev", "test"})
@Configuration
public class EmbeddedRedisConfig {

    private final RedisServer redisServer;

    public EmbeddedRedisConfig(RedisProperties redisProperties) throws IOException {
        int port = PortUtils.isPortRunning(redisProperties.getPort()) ?
                PortUtils.findAvailablePort() : redisProperties.getPort();

        this.redisServer = new RedisServer(port);
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
