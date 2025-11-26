package com.nhnacademy.byeol23front.couponset.coupon.client;

import com.nhnacademy.byeol23front.couponset.coupon.dto.CouponIssueRequestDto;
import com.nhnacademy.byeol23front.couponset.coupon.dto.IssuedCouponInfoResponseDto;
import com.nhnacademy.byeol23front.couponset.coupon.dto.UsedCouponInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "BYEOL23-GATEWAY", contextId = "couponApiClient")
public interface CouponApiClient {

    @PostMapping("/api/coupon")
    ResponseEntity<String> issueCoupon(@RequestBody CouponIssueRequestDto request);

    @GetMapping("/api/coupon/issued")
    ResponseEntity<List<IssuedCouponInfoResponseDto>> getIssuedCoupons();

    @GetMapping("/api/coupon/used")
    ResponseEntity<List<UsedCouponInfoResponseDto>> getUsedCoupons();
}
