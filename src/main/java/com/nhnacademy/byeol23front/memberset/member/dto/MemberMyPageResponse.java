package com.nhnacademy.byeol23front.memberset.member.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MemberMyPageResponse(
        String loginId,
        String memberName,
        String nickname,
        String phoneNumber,
        String email,
        LocalDate birthDate,
        BigDecimal currentPoint,
        Role memberRole,
        String gradeName
) {
}
