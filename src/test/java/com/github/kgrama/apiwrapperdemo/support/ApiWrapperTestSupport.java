package com.github.kgrama.apiwrapperdemo.support;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockWebServer;

@Configuration
public class ApiWrapperTestSupport {
	
	@Bean(destroyMethod = "shutdown")
	public MockWebServer getMockWebServer() throws IOException {
		var mockBackend = new MockWebServer();
		mockBackend.start();
		return mockBackend;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}
}
