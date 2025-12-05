package com.nhnacademy.byeol23front.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 로그인 요청 DTO")
public record SocialLoginRequest(
	@Schema(description = "소셜 로그인 제공자에서 받은 사용자 ID (예: 페이코 ID)", example = "payco123", required = true)
	String loginId
) {
}
