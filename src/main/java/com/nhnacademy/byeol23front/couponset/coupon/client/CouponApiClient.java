package com.nhnacademy.byeol23front.couponset.coupon.client;

import com.nhnacademy.byeol23front.couponset.coupon.dto.CouponIssueRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "BYEOL23-GATEWAY", contextId = "couponApiClient")
public interface CouponApiClient {

    @PostMapping("/api/coupon")
    ResponseEntity<String> issueCoupon(@RequestBody CouponIssueRequestDto request);

}
