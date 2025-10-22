package com.nhnacademy.byeol23front.couponset.couponpolicy.dto;

import java.math.BigDecimal;

public record CouponPolicyCreateRequest(
        String policyName,
        Long criterionPrice,
        Integer discountRate,
        BigDecimal discountLimit,
        BigDecimal discountAmount
) {

}
