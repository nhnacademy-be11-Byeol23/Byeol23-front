package com.nhnacademy.byeol23front.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.byeol23front.commons.exception.FeignExceptionDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class CommonFeignConfig {
	@Bean
	@Primary
	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper mapper = builder.createXmlMapper(false).build();

		mapper.registerModule(new JavaTimeModule());

		// record 생성자에 없는 필드도 null로 허용
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
		// 모르는 필드는 무시
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return mapper;
	}


	@Bean
	public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
		return new FeignExceptionDecoder(objectMapper);
	}

}
