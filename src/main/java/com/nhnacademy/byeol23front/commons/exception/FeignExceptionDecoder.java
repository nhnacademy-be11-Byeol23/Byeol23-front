package com.nhnacademy.byeol23front.commons.exception;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.bookset.contributor.exception.ContributorAlreadyExistsException;
import com.nhnacademy.byeol23front.bookset.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.byeol23front.bookset.tag.exception.TagNotFoundException;

import feign.Response;
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
		String message = "Unexpected error from backend";
		int status = response.status();
		ErrorResponse errorResponse = new ErrorResponse(400, "Default Error Response", "", LocalDateTime.now());

		if (response.body() == null){
			throw new RuntimeException("Response body of error response is null");
		}

		try {
			errorResponse = objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
			if (errorResponse.message() != null) {
				message = errorResponse.message();
			}
		} catch (IOException e) {
			message = "Failed to parse error response from backend";
		}


		if (status == HttpStatus.SC_UNAUTHORIZED) {
			//TODO: 인증과정에서 거부되었을 때의 반응 채울 것
		}



		if (status == HttpStatus.SC_CONFLICT && errorResponse.path().equals("/api/tags")){
			return new TagAlreadyExistsException(message, errorResponse.timestamp());
		}
		if (status == HttpStatus.SC_NOT_FOUND && errorResponse.path().equals("/api/tags")){
			return new TagNotFoundException(message, errorResponse.timestamp());
		}
		if (status == HttpStatus.SC_NOT_FOUND && errorResponse.path().equals("/api/cont")){
			return new ContributorAlreadyExistsException(message, errorResponse.timestamp());
		}

		return null;
	}
}
