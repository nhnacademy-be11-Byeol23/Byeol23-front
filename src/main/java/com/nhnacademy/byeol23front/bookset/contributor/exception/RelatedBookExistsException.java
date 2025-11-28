package com.nhnacademy.byeol23front.bookset.contributor.exception;

import java.time.LocalDateTime;

import lombok.Getter;

public class RelatedBookExistsException extends RuntimeException {
	@Getter
	private LocalDateTime timeStamp;
	public RelatedBookExistsException(String message, LocalDateTime timeStamp) {
		super(message);
		this.timeStamp = timeStamp;
	}
}
