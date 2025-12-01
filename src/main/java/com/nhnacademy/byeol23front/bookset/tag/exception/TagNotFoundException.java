package com.nhnacademy.byeol23front.bookset.tag.exception;

import java.time.LocalDateTime;

import lombok.Getter;

public class TagNotFoundException extends RuntimeException {

	@Getter
	private final LocalDateTime timeStamp;

	public TagNotFoundException(
		String message,
		LocalDateTime timeStamp
		) {
		super(message);
		this.timeStamp = timeStamp;
	}
}
