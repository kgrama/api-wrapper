package com.github.kgrama.apiwrapperdemo.config;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonModuleConfig {
	
	@Bean
	public Module orgJacksonModule() {
		var module = new SimpleModule();
		module.addSerializer(JSONObject.class, new JSONObjectCustomSerialise());
		return module;
	}
}
