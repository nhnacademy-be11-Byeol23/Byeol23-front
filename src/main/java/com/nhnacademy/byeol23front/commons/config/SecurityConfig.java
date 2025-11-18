package com.nhnacademy.byeol23front.commons.config;

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
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)   // 기본 로그인 폼
			.httpBasic(AbstractHttpConfigurer::disable)   // 기본 인증
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				//인증이 필요없는 URI 저장
				.requestMatchers(
					"/",
					"/error",
					"/members/login",
					"/members/register"
				).permitAll()
					.anyRequest().permitAll() //임시
				//인증이 필요한 나머지 URI에 대해서
				//.anyRequest().authenticated()
			)
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtParser),
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}

}
