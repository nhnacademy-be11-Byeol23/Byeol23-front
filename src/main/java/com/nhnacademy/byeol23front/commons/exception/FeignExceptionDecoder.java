package com.nhnacademy.byeol23front.commons.exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.http.HttpStatus;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.bookset.contributor.exception.ContributorAlreadyExistsException;
import com.nhnacademy.byeol23front.bookset.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.byeol23front.bookset.tag.exception.TagNotFoundException;
import com.nhnacademy.byeol23front.orderset.order.exception.OrderTemporaryStorageException;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FeignExceptionDecoder implements ErrorDecoder {

	private final ObjectMapper objectMapper;

	@Override
	public Exception decode(String methodKey, Response response) {
		log.info("response:{}", response);

		String errorResponseBody = null;
		try {
			errorResponseBody = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
		} catch (IOException e) {
			log.error("디코딩 실패");
			return new DefaultException("Default exception");
		}
		ErrorResponse errorResponse = null;
		try {
			if(errorResponseBody != null && !errorResponseBody.isBlank()) {
				errorResponse = objectMapper.readValue(errorResponseBody, ErrorResponse.class);
			}
		} catch (Exception e) {
			log.error("JSON 파싱 실패");
			return new DefaultException("Default exception");
		}

		int status = errorResponse.status();
		String message = errorResponse.message();
		String path = errorResponse.path();
		LocalDateTime time = errorResponse.timestamp();

		if (response.body() == null){
			throw new RuntimeException("Response body of error response is null");
		}

		if (status == HttpStatus.SC_UNAUTHORIZED) {
			//TODO: 인증과정에서 거부되었을 때의 반응 채울 것
		}

		if (status == HttpStatus.SC_CONFLICT && path.equals("/api/tags")){
			return new TagAlreadyExistsException(message, time);
		}
		if (status == HttpStatus.SC_NOT_FOUND && path.equals("/api/tags")){
			return new TagNotFoundException(message, time);
		}
		if (status == HttpStatus.SC_NOT_FOUND && path.equals("/api/cont")){
			return new ContributorAlreadyExistsException(message, time);
		}
		if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR && path.equals("/api/orders")) {
			return new OrderTemporaryStorageException(message, time);
		}


		return new DefaultException("Default exception");
	}
}
