package com.kdpark.sickdan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SickdanApplication {
	private static final String PROPERTIES = "spring.config.location=" +
			"classpath:/application.yml" +
			",classpath:/secret.yml";

	public static void main(String[] args) {
		new SpringApplicationBuilder(SickdanApplication.class)
				.properties(PROPERTIES)
				.run(args);
	}

}
