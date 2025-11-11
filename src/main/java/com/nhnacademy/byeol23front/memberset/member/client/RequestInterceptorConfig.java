package com.nhnacademy.byeol23front.memberset.member.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RequestInterceptorConfig {

	@Bean
	public RequestInterceptor preHandler() {
		return (RequestTemplate template) -> {
			var attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
			if(attrs==null) return;

			HttpServletRequest req = attrs.getRequest();

			String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
			if(auth != null && !auth.isBlank()) {
				template.header(HttpHeaders.AUTHORIZATION, auth);
			}

			String cookie = req.getHeader(HttpHeaders.COOKIE);
			if(cookie != null && !cookie.isBlank()) {
				template.header(HttpHeaders.COOKIE, cookie);
			}

			log.info("[feign] auth={}, cookie={}", auth, cookie);
		};




	}
}
