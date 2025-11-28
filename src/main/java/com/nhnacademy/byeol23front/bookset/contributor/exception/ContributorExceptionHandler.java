package com.nhnacademy.byeol23front.bookset.contributor.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.byeol23front.commons.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ContributorExceptionHandler {

	@ExceptionHandler(ContributorNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleContributorNotFoundException(ContributorNotFoundException e, HttpServletRequest request){
		ErrorResponse error = new ErrorResponse(
			HttpStatus.NOT_FOUND.value(),
			e.getMessage(),
			request.getRequestURI(),
			e.getTimeStamp()
			);
		log.warn(error.toString());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(ContributorAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleContributorAlreadyExistsException(ContributorAlreadyExistsException e, HttpServletRequest request){
		ErrorResponse error = new ErrorResponse(
			HttpStatus.CONFLICT.value(),
			e.getMessage(),
			request.getRequestURI(),
			e.getTimeStamp()
		);
		log.error(error.toString());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(RelatedBookExistsException.class)
	public ResponseEntity<ErrorResponse> handleRelatedBookExistsException(RelatedBookExistsException e, HttpServletRequest request){
		ErrorResponse error = new ErrorResponse(
			HttpStatus.UNPROCESSABLE_ENTITY.value(),
			e.getMessage(),
			request.getRequestURI(),
			e.getTimeStamp()
		);
		log.error(error.toString());
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
	}

}
