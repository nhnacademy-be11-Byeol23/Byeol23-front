package com.nhnacademy.byeol23front.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 중복 체크 응답 DTO")
public record ValueDuplicationCheckResponse(
		@Schema(description = "아이디 중복 여부", example = "false")
		boolean isDuplicatedId,

		@Schema(description = "닉네임 중복 여부", example = "true")
		boolean isDuplicatedNickname,

		@Schema(description = "이메일 중복 여부", example = "false")
		boolean isDuplicatedEmail,

		@Schema(description = "전화번호 중복 여부", example = "false")
		boolean isDuplicatedPhoneNumber
) {}
