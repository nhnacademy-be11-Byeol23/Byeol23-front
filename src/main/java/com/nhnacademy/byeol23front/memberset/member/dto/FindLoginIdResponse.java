package com.nhnacademy.byeol23front.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "아이디 중복 체크 응답 DTO")
public record FindLoginIdResponse(
		@Schema(description = "중복 여부", example = "true")
		boolean isDuplicated
) {
}
