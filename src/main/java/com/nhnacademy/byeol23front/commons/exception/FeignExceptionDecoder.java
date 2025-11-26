package com.nhnacademy.byeol23front.commons.exception;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class FeignExceptionDecoder implements ErrorDecoder {

	private final ObjectMapper objectMapper;
	@Override
	public Exception decode(String methodKey, Response response) {
		log.info("response:{}", response);
		return null;
	}
}
