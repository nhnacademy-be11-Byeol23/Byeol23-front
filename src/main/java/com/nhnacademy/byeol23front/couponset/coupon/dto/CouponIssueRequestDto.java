package com.nhnacademy.byeol23front.couponset.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record CouponIssueRequestDto(
        Long couponPolicyId,
        String couponName,
        LocalDate expiredDate
) {
}
