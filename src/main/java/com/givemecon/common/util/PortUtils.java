package com.givemecon.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.givemecon.common.util.OS.*;

/**
 * Reference: <a href="https://jojoldu.tistory.com/297">https://jojoldu.tistory.com/297</a>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PortUtils {

    /**
     * port가 현재 사용 중인지 확인
     */
    public static boolean isPortRunning(int port) throws IOException {
        return isProcessRunning(executeGrepProcessCommand(port));
    }

    /**
     * 현재 PC/서버에서 사용 가능한 포트 조회
     */
    public static int findAvailablePort() throws IOException {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);

            if (!isProcessRunning(process)) {
                return port;
            }
        }

        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }

    /**
     * 해당 port를 사용 중인 프로세스 확인하는 sh 실행
     */
    private static Process executeGrepProcessCommand(int port) throws IOException {
        OS os = OS.detectOS();
        String[] shell;

        if (os == WINDOWS) {
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
    private static boolean isProcessRunning(Process process) {
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
