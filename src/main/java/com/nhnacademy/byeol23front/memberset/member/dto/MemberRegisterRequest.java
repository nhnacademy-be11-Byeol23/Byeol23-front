package com.nhnacademy.byeol23front.memberset.member.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import com.nhnacademy.byeol23front.memberset.domain.RegistrationSource;
import com.nhnacademy.byeol23front.memberset.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원가입 요청 DTO")
public class MemberRegisterRequest {

	@Schema(description = "로그인 아이디", example = "user01")
	String loginId;

	@Schema(description = "로그인 비밀번호", example = "P@ssw0rd!")
	String loginPassword;

	@Schema(description = "회원 이름", example = "홍길동")
	String memberName;

	@Schema(description = "닉네임", example = "별이삼")
	String nickname;

	@Schema(description = "이메일", example = "user01@example.com")
	String email;

	@Schema(description = "전화번호", example = "010-1234-5678")
	String phoneNumber;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "생년월일", example = "1990-01-01")
	LocalDate birthDate;

	@Schema(description = "회원 권한", example = "USER")
	Role memberRole;

	@Schema(description = "가입 경로", example = "LOCAL")
	RegistrationSource joinedFrom;
}
