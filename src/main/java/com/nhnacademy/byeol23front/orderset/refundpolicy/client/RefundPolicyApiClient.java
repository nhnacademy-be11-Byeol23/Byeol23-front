package com.nhnacademy.byeol23front.orderset.refundpolicy.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "refundPolicyApiClient")
public interface RefundPolicyApiClient {

}
