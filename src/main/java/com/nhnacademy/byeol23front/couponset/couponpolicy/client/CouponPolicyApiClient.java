package com.nhnacademy.byeol23front.couponset.couponpolicy.client;

import com.nhnacademy.byeol23front.couponset.couponpolicy.dto.CouponPolicyCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

//api/coupon-policy/create
@FeignClient(name = "BYEOL23-GATEWAY", contextId = "couponPolicyApiClient")
public interface CouponPolicyApiClient {

    @PostMapping("api/coupon-policy/create")
    void couponPolicyCreate(CouponPolicyCreateRequest couponPolicyCreateRequest);

}
