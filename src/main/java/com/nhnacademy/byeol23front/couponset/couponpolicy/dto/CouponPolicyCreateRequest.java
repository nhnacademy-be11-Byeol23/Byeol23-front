package com.nhnacademy.byeol23front.couponset.couponpolicy.dto;

import java.math.BigDecimal;

public record CouponPolicyCreateRequest(
        String policyName,
        BigDecimal criterionPrice,
        Integer discountRate,
        BigDecimal discountLimit,
        BigDecimal discountAmount,
        String couponScope,
        Long categoryIds,
        Long bookId
) {
}
