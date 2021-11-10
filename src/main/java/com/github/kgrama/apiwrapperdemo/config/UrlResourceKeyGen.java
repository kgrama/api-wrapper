package com.github.kgrama.apiwrapperdemo.config;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UrlResourceKeyGen implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		
		try {
			var concatenatedString = (String) params[0] + (String) params[1];
			var digest = MessageDigest.getInstance("SHA3-256");
			var hashbytes = digest.digest(
				concatenatedString.getBytes(StandardCharsets.UTF_8));
			log.debug("input: {},  hex {}",StringUtils.arrayToDelimitedString(params, "_"), 
					javax.xml.bind.DatatypeConverter.printHexBinary(hashbytes));
			return javax.xml.bind.DatatypeConverter.printHexBinary(hashbytes);
		} catch (Exception e) {
			log.error("Exception in keygen returning random key {}" , e);
		}
		return UUID.randomUUID().toString();
	}

}
