package com.nhnacademy.byeol23front.memberset.member.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class MemberRegisterRequest {
	String loginId;
	String password;
	String memberName;
	String nickname;
	String email;
	String phoneNumber;
	LocalDate birthDate;
}
