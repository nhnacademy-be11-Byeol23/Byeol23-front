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
		String requestUrl = template.url();

		String newAccessToken = (String) request.getAttribute("NEW_ACCESS_TOKEN");

		boolean isRefreshRequest = requestUrl.contains("/auth/refresh");
		boolean isLogoutRequest = requestUrl.contains("/auth/logout");
		boolean isLoginRequest = requestUrl.contains("/auth/login");

		Cookie[] cookies = request.getCookies();
		
		// Refresh-Token과 guestId 쿠키 처리
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("Refresh-Token".equals(cookie.getName())) {
					String tokenValue = cookie.getValue();
					template.header("Cookie", "Refresh-Token=" + tokenValue);
				}
				
				if ("guestId".equals(cookie.getName())) {
					String guestId = cookie.getValue();
					template.header("Cookie", "guestId=" + guestId);
				}
			}
		}

		// Access-Token 처리: NEW_ACCESS_TOKEN이 있으면 우선 사용, 없으면 쿠키에서 읽기
		if (!isRefreshRequest && !isLogoutRequest && !isLoginRequest) {
			String accessToken = null;
			
			// 1. NEW_ACCESS_TOKEN attribute가 있으면 우선 사용 (필터에서 새로 발급한 토큰)
			if (newAccessToken != null && !newAccessToken.isEmpty()) {
				accessToken = newAccessToken;
				log.debug("Using NEW_ACCESS_TOKEN from request attribute for URL: {}", requestUrl);
			} 
			// 2. 없으면 쿠키에서 읽기
			else if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("Access-Token".equals(cookie.getName())) {
						String tokenValue = cookie.getValue();
						accessToken = tokenValue.startsWith("Bearer ")
							? tokenValue.substring(7)
							: tokenValue;
						log.debug("Using Access-Token from cookie for URL: {}", requestUrl);
						break;
					}
				}
			}
			
			// 3. Access-Token이 있으면 Authorization 헤더에 추가
			if (accessToken != null && !accessToken.isEmpty()) {
				template.header(HttpHeaders.AUTHORIZATION, STR."Bearer \{accessToken}");
			} else {
				log.warn("No Access-Token available for Feign request to: {}", requestUrl);
			}
		}
		};
	}
}
