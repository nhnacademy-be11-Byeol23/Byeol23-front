package com.nhnacademy.byeol23front.commons.exception;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nhnacademy.byeol23front.commons.deserializer.OffsetToLocalDateTimeDeserializer;

import java.time.LocalDateTime;

public record GatewayErrorResponse(int status,
                                   String error,
                                   String path,
                                   @JsonDeserialize(using = OffsetToLocalDateTimeDeserializer.class)
                                   LocalDateTime timestamp) {
}
