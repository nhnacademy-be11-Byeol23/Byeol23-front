package com.nhnacademy.byeol23front.memberset.member.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.byeol23front.memberset.grade.dto.AllGradeResponse;

import com.nhnacademy.byeol23front.memberset.addresses.dto.AddressResponse;

public record MemberMyPageResponse(
        String loginId,
        String memberName,
        String nickname,
        String phoneNumber,
        String email,
        LocalDate birthDate,
        BigDecimal currentPoint,
        Role memberRole,
        String gradeName,
		List<AllGradeResponse> grades,
		AddressResponse address
) {
}
