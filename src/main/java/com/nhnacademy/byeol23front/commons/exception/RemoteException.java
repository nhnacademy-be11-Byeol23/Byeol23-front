package com.nhnacademy.byeol23front.commons.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RemoteException extends RuntimeException {
	private final ErrorResponse errorResponse;

	public RemoteException(String message, ErrorResponse errorResponse) {
		super(message);
		this.errorResponse = errorResponse;
	}

	public ErrorResponse getError() {
		return this.errorResponse;
	}
}
