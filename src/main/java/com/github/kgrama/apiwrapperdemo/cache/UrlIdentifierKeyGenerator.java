package com.github.kgrama.apiwrapperdemo.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component
public class UrlIdentifierKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		if("findRequestedResource".equals(method.getName() )){
			
		}
		return null;
	}

}
