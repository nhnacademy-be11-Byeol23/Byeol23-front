package com.nhnacademy.byeol23front.memberset.member.dto;

import java.time.LocalDate;

public record MemberUpdateRequest(
	String memberName,
	String nickname,
	String phoneNumber,
	String email,
	LocalDate birthDate
) {
}
