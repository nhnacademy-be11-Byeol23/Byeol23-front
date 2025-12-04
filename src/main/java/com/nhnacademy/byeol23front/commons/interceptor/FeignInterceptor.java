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
		String accessToken = null;

		// Refresh-Token과 guestId 쿠키 처리
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("Refresh-Token".equals(cookie.getName()) && template.path().startsWith("/auth")) {
					refreshToken = cookie.getValue();

					template.header("Refresh-Token", refreshToken);
				}
				if ("Access-Token".equals(cookie.getName()) && !template.path().startsWith("/api/categories") && template.path().startsWith("/api")) {
					accessToken = cookie.getValue();

					template.header(HttpHeaders.AUTHORIZATION, accessToken);
					template.header("Access-Token", accessToken);		//임시용
				}
				
				if ("guestId".equals(cookie.getName())) {
					String guestId = cookie.getValue();
					template.header("Cookie", "guestId=" + guestId);
				}
			}
		}
		};
	}
}
