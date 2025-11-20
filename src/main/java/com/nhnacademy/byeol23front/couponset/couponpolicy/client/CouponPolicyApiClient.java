package com.nhnacademy.byeol23front.couponset.couponpolicy.client;

import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyCreateRequest;
import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

//api/coupon-policy/create
@FeignClient(name = "BYEOL23-GATEWAY", contextId = "couponPolicyApiClient")
public interface CouponPolicyApiClient {

    @PostMapping("/api/coupon-policy/create")
    void couponPolicyCreate(@RequestBody CouponPolicyCreateRequest couponPolicyCreateRequest);

    @GetMapping("/api/coupon-policy")
    ResponseEntity<List<CouponPolicyInfoResponse>> getCouponPolicies();


}
