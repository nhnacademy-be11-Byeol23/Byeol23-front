package com.nhnacademy.byeol23front.orderset.order.exception;

import java.time.LocalDateTime;

import lombok.Getter;

public class OrderTemporaryStorageException extends RuntimeException {

	@Getter
	private LocalDateTime time;

	public OrderTemporaryStorageException(String message) {
		super(message);
	}

	public OrderTemporaryStorageException(String message, LocalDateTime time) {
		super(message);
		this.time =  time;
	}
}
