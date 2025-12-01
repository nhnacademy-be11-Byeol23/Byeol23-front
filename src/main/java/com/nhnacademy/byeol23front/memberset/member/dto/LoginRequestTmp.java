package com.nhnacademy.byeol23front.memberset.member.dto;

import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestTmp {
	String loginId;
	String loginPassword;
	@Null
	private Long bookId;
	@Null
	private Integer quantity;
}
