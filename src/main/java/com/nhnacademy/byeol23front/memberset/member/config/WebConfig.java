package com.nhnacademy.byeol23front.memberset.member.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nhnacademy.byeol23front.memberset.member.client.RequestInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private RequestInterceptor requestInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestInterceptor)
			.addPathPatterns("/**")  // 모든 경로에 적용
			.excludePathPatterns(
				"/members/login",    // 로그인 페이지 제외
				"/members/register", // 회원가입 페이지 제외
				"/",                 // 메인 페이지 제외
				"/assets/**",        // 정적 리소스 제외
				"/error"             // 에러 페이지 제외
			);
	}
}