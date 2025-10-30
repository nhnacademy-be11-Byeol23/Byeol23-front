package com.nhnacademy.byeol23front.memberset.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain httpToHttps(HttpSecurity http) throws Exception {
		http.requiresChannel(ch -> ch.anyRequest().requiresSecure()) // http → https 302 리다이렉트
			.headers(h -> h.httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).preload(true)));
		return http.build();
	}
}
