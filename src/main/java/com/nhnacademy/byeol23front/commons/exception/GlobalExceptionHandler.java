package com.nhnacademy.byeol23front.commons.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(KeyLoadFailureException.class)
	public ErrorResponse handleKeyLoadFailureException(KeyLoadFailureException e, HttpServletRequest request) {
		return new ErrorResponse(
			HttpStatus.UNAUTHORIZED.value(),  //401
			e.getMessage(),
			request.getRequestURI(),
			LocalDateTime.now()
		);
	}

}
