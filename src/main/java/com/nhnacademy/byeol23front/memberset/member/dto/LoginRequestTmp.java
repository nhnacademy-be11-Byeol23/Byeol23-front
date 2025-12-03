package com.nhnacademy.byeol23front.memberset.member.dto;

import java.util.List;

import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestTmp {
	String loginId;
	String loginPassword;
	String redirectUrl;
	private List<Long> bookIds;
	private List<Integer> quantities;
}
