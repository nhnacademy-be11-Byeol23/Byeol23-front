package com.nhnacademy.byeol23front.bookset.contributor.exception;

import java.time.LocalDateTime;

import lombok.Getter;

public class ContributorNotFoundException extends RuntimeException {
	@Getter
	private LocalDateTime timeStamp;
	public ContributorNotFoundException(String message, LocalDateTime timeStamp) {
		super(message);
		this.timeStamp = timeStamp;
	}
}
