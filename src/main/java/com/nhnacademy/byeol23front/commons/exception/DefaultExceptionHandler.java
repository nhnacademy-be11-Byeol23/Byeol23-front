package com.nhnacademy.byeol23front.commons.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class DefaultExceptionHandler {

	@ExceptionHandler(DefaultException.class)
	public String handleDefaultException(DefaultException e, HttpServletRequest req){
		log.info(e.getMessage());
		return "/error";
	}

}
