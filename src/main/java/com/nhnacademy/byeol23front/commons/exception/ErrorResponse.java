package com.nhnacademy.byeol23front.commons.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    int status,
    String message,
    String path,
    LocalDateTime timestamp
) {}

