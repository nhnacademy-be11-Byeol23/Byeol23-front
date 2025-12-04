package com.nhnacademy.byeol23front.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

	@Schema(description = "로그인 아이디", example = "user01")
	String loginId;

	@Schema(description = "로그인 비밀번호", example = "P@ssw0rd!")
	String loginPassword;
}
