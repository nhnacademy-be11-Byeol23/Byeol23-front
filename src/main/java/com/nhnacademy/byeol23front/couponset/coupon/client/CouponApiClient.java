package com.nhnacademy.byeol23front.couponset.coupon.client;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "BYEOL23-GATEWAY", contextId = "couponApiClient")
public interface CouponApiClient {


}
