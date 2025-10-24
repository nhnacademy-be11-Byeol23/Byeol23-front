package com.nhnacademy.byeol23front.couponset.couponpolicy.client;

import org.springframework.cloud.openfeign.FeignClient;
///api/coupon-policy/create
@FeignClient(name = "couponPolicyApiClient", url = "${backend.api.url}")
public interface CouponPolicyApiClient {

}
