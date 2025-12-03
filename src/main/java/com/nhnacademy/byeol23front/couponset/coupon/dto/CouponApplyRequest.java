package com.nhnacademy.byeol23front.couponset.coupon.dto;

import java.util.List;

public record CouponApplyRequest(
        Long couponId,
        List<OrderItemRequest> orderItems
) {}