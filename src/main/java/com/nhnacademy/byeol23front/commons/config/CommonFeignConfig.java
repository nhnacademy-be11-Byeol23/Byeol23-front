package com.nhnacademy.byeol23front.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.commons.exception.FeignExceptionDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class CommonFeignConfig {
	// private final FeignExceptionDecoder feignExceptionDecoder;
	//
	// public FeignConfig(FeignExceptionDecoder feignExceptionDecoder) {
	// 	this.feignExceptionDecoder = feignExceptionDecoder;
	// }

	@Bean
	public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
		return new FeignExceptionDecoder(objectMapper);
	}

}
