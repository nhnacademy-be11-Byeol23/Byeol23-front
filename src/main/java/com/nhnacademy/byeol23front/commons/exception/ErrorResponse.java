package com.nhnacademy.byeol23front.commons.exception;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nhnacademy.byeol23front.commons.deserializer.OffsetToLocalDateTimeDeserializer;

import java.time.LocalDateTime;

public record ErrorResponse(int status,
                            String message,
                            @JsonDeserialize(using = OffsetToLocalDateTimeDeserializer.class)
                            LocalDateTime timestamp) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, LocalDateTime.now());
    }

    public static ErrorResponse defaultErrorResponse() {
        return new ErrorResponse(500, "요청 처리 중 예외가 발생했습니다 잠시 후 다시 시도해 주세요.", LocalDateTime.now());
    }
}
