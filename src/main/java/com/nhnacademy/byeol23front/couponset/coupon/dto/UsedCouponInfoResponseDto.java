package com.nhnacademy.byeol23front.couponset.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsedCouponInfoResponseDto(
        Long couponPolicyId,
        Long couponId,
        String couponName,
        String couponPolicyType,
        String discount,
        BigDecimal discountLimit,
        BigDecimal criterionPrice,
        LocalDate createdDate,
        LocalDateTime usedAt
) {
}
