package com.nhnacademy.byeol23front.memberset.member.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.nhnacademy.byeol23front.memberset.domain.RegistrationSource;
import com.nhnacademy.byeol23front.memberset.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class MemberRegisterRequest {
	String loginId;
	String loginPassword;
	String memberName;
	String nickname;
	String email;
	String phoneNumber;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate birthDate;
	Role memberRole;
	RegistrationSource joinedFrom;
}
