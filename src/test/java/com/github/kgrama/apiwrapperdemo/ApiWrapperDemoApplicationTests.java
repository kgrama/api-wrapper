package com.github.kgrama.apiwrapperdemo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
class ApiWrapperDemoApplicationTests {
	
	@Autowired(required = false)
	private MockWebServer mockWebserver;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void verifyBeans() {
		assertNotNull(mockWebserver, "Mock webserver should not be null");
	}

}
