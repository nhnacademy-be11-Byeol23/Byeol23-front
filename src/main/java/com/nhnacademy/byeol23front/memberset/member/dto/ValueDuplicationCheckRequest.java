package com.nhnacademy.byeol23front.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보(아이디/닉네임/전화번호/이메일) 중복 체크 요청 DTO")
public record ValueDuplicationCheckRequest(
		@Schema(description = "로그인 아이디", example = "user01")
		String loginId,

		@Schema(description = "닉네임", example = "별이삼")
		String nickname,

		@Schema(description = "전화번호", example = "010-1234-5678")
		String phoneNumber,

		@Schema(description = "이메일", example = "user01@example.com")
		String email
){}
