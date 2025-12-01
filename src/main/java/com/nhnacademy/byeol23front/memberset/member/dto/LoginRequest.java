package com.nhnacademy.byeol23front.memberset.member.dto;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
public class LoginRequest {
	String loginId;
	String loginPassword;
}
