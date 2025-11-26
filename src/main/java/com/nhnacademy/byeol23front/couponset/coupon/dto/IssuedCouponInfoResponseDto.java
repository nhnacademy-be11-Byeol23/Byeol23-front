package com.nhnacademy.byeol23front.couponset.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IssuedCouponInfoResponseDto(
        Long couponPolicyId,
        Long couponId,
        String couponName,
        String couponPolicyType,
        String discount,
        BigDecimal discountLimit,
        BigDecimal criterionPrice,
        LocalDate createdDate,
        LocalDate expiredDate,
        Boolean status
) {
}
