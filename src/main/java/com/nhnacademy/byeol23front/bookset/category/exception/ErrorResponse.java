package com.nhnacademy.byeol23front.bookset.category.exception;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String message, LocalDateTime timestamp) {
}
