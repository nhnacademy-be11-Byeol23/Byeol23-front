package com.nhnacademy.byeol23front.commons.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    int status,
    String message,
    String path,
    LocalDateTime timestamp
) {
	private static StringBuilder sb = new StringBuilder();

	@Override
	public String toString(){
		return String.valueOf(sb.append("[ERROR RESPONSE] ")
			.append(this.status()).append(" ")
			.append(this.message()).append(" ")
			.append(this.path()).append(" ")
			.append(this.timestamp()));
	}
}

