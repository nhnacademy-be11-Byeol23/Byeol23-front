package com.nhnacademy.byeol23front.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답 DTO (발급된 토큰 정보)")
public record LoginResponse(
		@Schema(description = "발급된 Access Token", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
		String accessToken,

		@Schema(description = "발급된 Refresh Token", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
		String refreshToken
) {
}
