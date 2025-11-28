package com.nhnacademy.byeol23front.bookset.tag.exception;

import java.time.LocalDateTime;

import lombok.Getter;

public class TagAlreadyExistsException extends RuntimeException {
	@Getter
	private LocalDateTime timeStamp;

	public TagAlreadyExistsException(String message, LocalDateTime timeStamp) {
		super(message);
		this.timeStamp = timeStamp;
	}
}
