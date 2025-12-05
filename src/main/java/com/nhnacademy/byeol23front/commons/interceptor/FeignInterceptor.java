package com.nhnacademy.byeol23front.commons.interceptor;

import com.nhnacademy.byeol23front.auth.feign.TokenContext;
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

			String accessToken = null;
			String refreshToken = null;
			String guestId = null;

			String tokenFromContext = TokenContext.get();
			if (tokenFromContext != null && !tokenFromContext.isBlank()
					&& template.path().startsWith("/api")) {
				accessToken = tokenFromContext;
			}

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					switch (cookie.getName()) {
						case "Access-Token" -> {
							if (accessToken == null
									&& template.path().startsWith("/api")
									&& !template.path().startsWith("/api/categories")) {
								accessToken = cookie.getValue();
							}
						}
						case "Refresh-Token" -> {
							if (template.path().startsWith("/auth")) {
								refreshToken = cookie.getValue();
							}
						}
						case "guestId" -> guestId = cookie.getValue();
					}
				}
			}

		if (accessToken != null && !accessToken.isBlank()
					&& template.path().startsWith("/api")) {

				String headerValue = accessToken.startsWith("Bearer ")
						? accessToken
						: "Bearer " + accessToken;

				template.header(HttpHeaders.AUTHORIZATION, headerValue);
				template.header("Access-Token", headerValue); // 임시용 유지하고 싶으면
			}

			// Refresh-Token 필요 시 헤더로 넣기 (Auth 관련 요청)
			if (refreshToken != null && !refreshToken.isBlank()
					&& template.path().startsWith("/auth")) {
				template.header("Refresh-Token", refreshToken);
			}

			// guestId를 쿠키 형태로 포워딩
			if (guestId != null && !guestId.isBlank() && accessToken==null) {
				template.header("Cookie", "guestId=" + guestId);
			}
		};
	}
}
