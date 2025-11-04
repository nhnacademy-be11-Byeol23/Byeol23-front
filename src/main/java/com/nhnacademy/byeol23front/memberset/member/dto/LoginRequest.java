package com.nhnacademy.byeol23front.memberset.member.dto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class LoginRequest {
	String loginId;
	String password;
}
