package com.nhnacademy.byeol23front.commons.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
	public ExpiredRefreshTokenException(String message) {
		super(message);
	}
}
