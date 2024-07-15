package com.givemecon.common.configuration;

import com.givemecon.common.auth.util.ClientUrlProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientUrlProperties.class)
public class ClientConfig {
}
