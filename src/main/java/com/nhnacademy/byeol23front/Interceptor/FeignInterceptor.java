package com.nhnacademy.byeol23front.Interceptor;

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
				String path = template.path();

				if(!path.startsWith("/books") && !path.startsWith("/members")) {
					return;
				}

				ServletRequestAttributes attrs =
					(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
				if(attrs == null) return;

				HttpServletRequest request = attrs.getRequest();

				Cookie[] cookies = request.getCookies();
				if (cookies == null) return;

				for (Cookie cookie : cookies) {
					if ("Refresh-Token".equals(cookie.getName())) {
						String tokenValue = cookie.getValue();
						log.info("Refresh-Token: {}", tokenValue);

						template.header("Cookie", "Refresh-Token=" + tokenValue);
					}
					if ("Access-Token".equals(cookie.getName())) {
						String tokenValue = cookie.getValue();
						log.info("Access-Token: {}", tokenValue);

						template.header("Cookie", "Access-Token=" + tokenValue);
					}
				}
			};
		}
}
