package com.github.kgrama.apiwrapperdemo.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
@SuppressWarnings({"rawtypes", "unchecked"})
public class CacheConfiguration  {
	
	@Bean
	public Caffeine caffeineConfig(@Value(value = "${processing.cache.time:5}") int cacheTimeout) {
	    return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES);
	}
	
	@Bean
	public CacheManager cacheManager(Caffeine caffeine) {
	    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
	    caffeineCacheManager.setCaffeine(caffeine);
	    return caffeineCacheManager;
	}
	
	@Bean(name = "urlResourceKeyGen") 
	public KeyGenerator getURLResourceKeyGenerator() {
		return new UrlResourceKeyGen();
	}
}

