package com.nhnacademy.byeol23front.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nhnacademy.byeol23front.commons.exception.FeignExceptionDecoder;

import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;

@Configuration
public class FeignConfig {
	private final FeignExceptionDecoder feignExceptionDecoder;

	public FeignConfig(FeignExceptionDecoder feignExceptionDecoder) {
		this.feignExceptionDecoder = feignExceptionDecoder;
	}

	@Bean
	public ErrorDecoder errorDecoder() {
		return feignExceptionDecoder;
	}

}
