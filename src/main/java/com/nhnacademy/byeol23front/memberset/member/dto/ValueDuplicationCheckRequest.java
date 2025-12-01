package com.nhnacademy.byeol23front.memberset.member.dto;

public record ValueDuplicationCheckRequest(
	String loginId,
	String nickname,
	String phoneNumber,
	String email
){}
