package com.nhnacademy.byeol23front.memberset.member.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 수정 요청 DTO")
public record MemberUpdateRequest(
		@Schema(description = "회원 이름", example = "홍길동")
		String memberName,

		@Schema(description = "닉네임", example = "별이삼")
		String nickname,

		@Schema(description = "전화번호", example = "010-1234-5678")
		String phoneNumber,

		@Schema(description = "이메일", example = "user01@example.com")
		String email,

		@Schema(description = "생년월일", example = "1990-01-01")
		LocalDate birthDate
) {
}