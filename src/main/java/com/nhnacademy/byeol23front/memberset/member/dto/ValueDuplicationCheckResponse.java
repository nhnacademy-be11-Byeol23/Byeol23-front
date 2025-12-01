package com.nhnacademy.byeol23front.memberset.member.dto;

public record ValueDuplicationCheckResponse(
	boolean isDuplicatedId,
	boolean isDuplicatedNickname,
	boolean isDuplicatedEmail,
	boolean isDuplicatedPhoneNumber
) {}
