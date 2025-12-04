package com.nhnacademy.byeol23front.commons.interceptor;

import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FeignInterceptor {

	@Bean
	public RequestInterceptor authHeaderInterceptor() {
		return template -> {

			ServletRequestAttributes attrs =
				(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
			if(attrs == null) return;

		HttpServletRequest request = attrs.getRequest();

		Cookie[] cookies = request.getCookies();

		String refreshToken = null;
		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		// Refresh-Token과 guestId 쿠키 처리
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("Refresh-Token".equals(cookie.getName())) {
					refreshToken = cookie.getValue();
				}
				
				if ("guestId".equals(cookie.getName())) {
					String guestId = cookie.getValue();
					template.header("Cookie", "guestId=" + guestId);
				}
			}
		}
			if (accessToken != null) {
				template.header(HttpHeaders.AUTHORIZATION, accessToken);
			}
			if (refreshToken != null) {
				template.header("Refresh-Token", refreshToken);
			}
		};
	}
}
