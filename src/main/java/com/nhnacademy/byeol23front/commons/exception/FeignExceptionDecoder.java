package com.nhnacademy.byeol23front.commons.exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.apache.http.HttpStatus;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.bookset.contributor.exception.ContributorAlreadyExistsException;
import com.nhnacademy.byeol23front.bookset.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.byeol23front.bookset.tag.exception.TagNotFoundException;

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

		//회원 관련 예외 처리
		if (status == HttpStatus.SC_BAD_REQUEST && path.startsWith("/api/members")) {

		}

		if (status == HttpStatus.SC_UNAUTHORIZED) {
				log.warn("401 ExpiredJwtException 발생 → ExpiredTokenException 던짐. methodKey={}, path={}", methodKey, path);
				return new ExpiredTokenException(message);
		}



		//태그 관련 예외 처리
		if (status == HttpStatus.SC_CONFLICT && errorResponse.path().equals("/api/tags")){
			return new TagAlreadyExistsException(message, errorResponse.timestamp());
		}
		if (status == HttpStatus.SC_NOT_FOUND && errorResponse.path().equals("/api/tags")){
			return new TagNotFoundException(message, errorResponse.timestamp());
		}
		if (status == HttpStatus.SC_NOT_FOUND && errorResponse.path().equals("/api/cont")){
			return new ContributorAlreadyExistsException(message, errorResponse.timestamp());
		}

		return new DefaultException("Default exception");
	}
}
