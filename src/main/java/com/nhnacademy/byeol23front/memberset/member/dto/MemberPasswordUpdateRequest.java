package com.nhnacademy.byeol23front.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비밀번호 변경 요청 DTO")
public record MemberPasswordUpdateRequest(
		@Schema(description = "현재 비밀번호", example = "OldP@ssw0rd!")
		String currentPassword,

		@Schema(description = "새 비밀번호", example = "NewP@ssw0rd!")
		String newPassword
) {
}