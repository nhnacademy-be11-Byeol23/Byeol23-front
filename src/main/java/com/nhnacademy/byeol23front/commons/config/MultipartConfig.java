package com.nhnacademy.byeol23front.commons.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import jakarta.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();

		// DataSize 유틸리티를 사용하는 것이 명확합니다.
		factory.setMaxFileSize(DataSize.ofMegabytes(10));
		factory.setMaxRequestSize(DataSize.ofMegabytes(11));

		return factory.createMultipartConfig();
	}
}