package com.nhnacademy.byeol23front.orderset.refund.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.orderset.refund.dto.RefundRequest;
import com.nhnacademy.byeol23front.orderset.refund.dto.RefundResponse;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "refundPolicyApiClient")
public interface RefundApiClient {
	@PostMapping("/api/refunds")
	ResponseEntity<RefundResponse> refund(@RequestBody RefundRequest refundRequest);
}
