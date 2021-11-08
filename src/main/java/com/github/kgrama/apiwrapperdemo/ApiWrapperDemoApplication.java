package com.github.kgrama.apiwrapperdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ApiWrapperDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiWrapperDemoApplication.class, args);
	}

}
