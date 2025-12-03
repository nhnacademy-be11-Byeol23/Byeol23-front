package com.nhnacademy.byeol23front.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import com.nhnacademy.byeol23front.commons.filter.JwtAuthenticationFilter;
import com.nhnacademy.byeol23front.commons.parser.JwtParser;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtParser jwtParser;
	private final MemberService memberService;


	@Value("${jwt.access-token.expiration}")
	private long accessTokenExp;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)   // 기본 로그인 폼
			.httpBasic(AbstractHttpConfigurer::disable)   // 기본 인증
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/mypage/**",
					"/admin/**",
					"/admin"
				).authenticated()
				.anyRequest().permitAll()
			)
			//매 요청마다 SecurityContextHolder에 Authentication을 채워줌
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtParser),
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}
}

