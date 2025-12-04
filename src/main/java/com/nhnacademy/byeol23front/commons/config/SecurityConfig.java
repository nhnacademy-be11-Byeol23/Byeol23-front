package com.nhnacademy.byeol23front.commons.config;

import com.nhnacademy.byeol23front.auth.CookieProperties;
import com.nhnacademy.byeol23front.auth.service.TokenRefreshService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nhnacademy.byeol23front.commons.filter.JwtAuthenticationFilter;
import com.nhnacademy.byeol23front.commons.parser.JwtParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtParser jwtParser;
	private final TokenRefreshService tokenRefreshService;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, CookieProperties cookieProperties) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)   // 기본 로그인 폼
			.httpBasic(AbstractHttpConfigurer::disable)   // 기본 인증
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/mypage/**").authenticated()
					.requestMatchers("/admin", "/admin/**").hasAuthority("ADMIN")
					.anyRequest().permitAll()
			)
			//매 요청마다 SecurityContextHolder에 Authentication을 채워줌
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtParser, tokenRefreshService, cookieProperties),
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}
}

