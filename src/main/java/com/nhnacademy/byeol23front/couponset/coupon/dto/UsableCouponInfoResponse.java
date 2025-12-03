package com.nhnacademy.byeol23front.couponset.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UsableCouponInfoResponse(
        Long couponId,
        String couponName,
        String discountType,
        BigDecimal discountValue,
        BigDecimal criterionPrice,
        BigDecimal discountLimit,
        String policyTargetType,
        LocalDate expiredDate
) {
}
