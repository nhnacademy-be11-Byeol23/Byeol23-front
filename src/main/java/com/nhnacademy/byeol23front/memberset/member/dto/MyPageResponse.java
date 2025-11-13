package com.nhnacademy.byeol23front.memberset.member.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MyPageResponse(
	String loginId,
	String memberName,
	String nickname,
	String phoneNumber,
	String email,
	LocalDate birthDate,
	BigDecimal currentPoint,
	String memberRole,
	String grade
) {
}
