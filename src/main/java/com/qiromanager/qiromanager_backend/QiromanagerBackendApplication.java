package com.qiromanager.qiromanager_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class QiromanagerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(QiromanagerBackendApplication.class, args);
	}

}
