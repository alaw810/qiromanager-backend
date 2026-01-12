package com.qiromanager.qiromanager_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class QiromanagerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(QiromanagerBackendApplication.class, args);
	}

}
