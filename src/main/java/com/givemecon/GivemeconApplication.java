package com.givemecon;

import com.givemecon.config.auth.util.ClientUrlUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ClientUrlUtils.class)
@SpringBootApplication
public class GivemeconApplication {

	public static void main(String[] args) {
		SpringApplication.run(GivemeconApplication.class, args);
	}

}
