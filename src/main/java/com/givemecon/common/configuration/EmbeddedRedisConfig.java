package com.givemecon.common.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;
import redis.embedded.model.OS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Reference: <a href="https://jojoldu.tistory.com/297">https://jojoldu.tistory.com/297</a>
 */
@Slf4j
@Profile("test")
@Configuration
public class EmbeddedRedisConfig {

    private final RedisServer redisServer;

    public EmbeddedRedisConfig(RedisProperties redisProperties) throws IOException {
        int port = isRedisRunning(redisProperties.getPort()) ? findAvailablePort() : redisProperties.getPort();
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }

    /**
     * Embedded Redis가 현재 실행 중인지 확인
     */
    private boolean isRedisRunning(int redisPort) throws IOException {
        return isRunning(executeGrepProcessCommand(redisPort));
    }

    /**
     * 현재 PC/서버에서 사용 가능한 포트 조회
     */
    public int findAvailablePort() throws IOException {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);

            if (!isRunning(process)) {
                return port;
            }
        }

        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }

    /**
     * 해당 port를 사용 중인 프로세스 확인하는 sh 실행
     */
    private Process executeGrepProcessCommand(int port) throws IOException {
        OS currentOS = OS.detectOS();
        String[] shell;

        if (currentOS == OS.WINDOWS) {
            String command = String.format("netstat -nao | find \"LISTENING\" | find \"%d\"", port);
            shell = new String[]{"cmd.exe", "/y", "/c", command};
        } else {
            String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
            shell = new String[]{"/bin/sh", "-c", command};
        }

        return Runtime.getRuntime().exec(shell);
    }

    /**
     * 해당 Process가 현재 실행 중인지 확인
     */
    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
            log.info("Exception occurred when checking process = ", e);
        }

        return StringUtils.hasText(pidInfo.toString());
    }
}
