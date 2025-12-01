package com.nhnacademy.byeol23front.commons.exception;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

import com.nhnacademy.byeol23front.memberset.member.dto.LoginResponse;
import feign.RetryableException;
import org.apache.http.HttpStatus;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.bookset.contributor.exception.ContributorAlreadyExistsException;
import com.nhnacademy.byeol23front.bookset.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.byeol23front.bookset.tag.exception.TagNotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
@RequiredArgsConstructor
public class FeignExceptionDecoder implements ErrorDecoder {

	private final ObjectMapper objectMapper;
	private final ErrorDecoder defaultDecoder = new Default();
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
//			try {
//				// Refresh 토큰으로 새 Access Token 발급
//				LoginResponse refreshResponse = refreshAccessToken();
//
//				if (refreshResponse != null) {
//					// 새 토큰을 쿠키에 저장
//					updateCookies(refreshResponse);
//
//					// 원래 요청을 재시도하기 위해 RetryableException 던지기
//					// Feign의 Retryer가 이를 처리하도록 함
//					return new RetryableException(
//							response.status(),
//							"Token refreshed, retrying request",
//							response.request().httpMethod(),
//							null,
//							response.request()
//					);
//				}
//			} catch (Exception e) {
//				log.error("Failed to refresh token", e);
//			}

			// Refresh 실패 시 기존처럼 AccessDeniedException 던지기
			return new AccessDeniedException("ACCESS-TOKEN-EXPIRED");
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

		return new DefaultException("Default exception");
	}

//	private LoginResponse refreshAccessToken() {
//		try {
//			ResponseEntity<LoginResponse> response = memberApiClient.refreshToken();
//			if (response != null && response.getBody() != null) {
//				log.info("Successfully refreshed access token");
//				return response.getBody();
//			}
//		} catch (Exception e) {
//			log.error("Failed to refresh token: {}", e.getMessage());
//		}
//		return null;
//	}

}
