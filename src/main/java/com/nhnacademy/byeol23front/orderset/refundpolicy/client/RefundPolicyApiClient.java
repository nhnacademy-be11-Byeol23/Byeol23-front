package com.nhnacademy.byeol23front.orderset.refundpolicy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.nhnacademy.byeol23front.orderset.refundpolicy.dto.RefundPolicyCreateRequest;
import com.nhnacademy.byeol23front.orderset.refundpolicy.dto.RefundPolicyCreateResponse;
import com.nhnacademy.byeol23front.orderset.refundpolicy.dto.RefundPolicyInfoResponse;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "refundPolicyApiClient")
public interface RefundPolicyApiClient {

	@GetMapping("/api/refund-policies")
	ResponseEntity<Page<RefundPolicyInfoResponse>> getAllRefundPolicies(Pageable pageable);

	@PostMapping("/api/refund-policies")
	ResponseEntity<RefundPolicyCreateResponse> createRefundPolicy(RefundPolicyCreateRequest refundPolicyCreateRequest);

}
