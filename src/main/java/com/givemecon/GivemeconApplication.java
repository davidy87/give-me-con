package com.givemecon;

import com.givemecon.util.aspect.log.LogAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(LogAspect.class)
@SpringBootApplication
public class GivemeconApplication {

	public static void main(String[] args) {
		SpringApplication.run(GivemeconApplication.class, args);
	}

}
