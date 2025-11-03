package com.nhnacademy.byeol23front.orderset.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;

@FeignClient(name = "BYEOL23-GATEWAY", contextId = "deliveryApiClient")
public interface DeliveryApiClient {

	@GetMapping("/api/delivery-policies")
	ResponseEntity<Page<DeliveryPolicyInfoResponse>> getDeliveryPolicies(Pageable pageable);

	@PostMapping("/api/delivery-policies")
	ResponseEntity<DeliveryPolicyCreateResponse> createDeliveryPolicy(DeliveryPolicyCreateRequest deliveryPolicyCreateRequest);

	@GetMapping("/api/delivery-policies/current")
	ResponseEntity<DeliveryPolicyInfoResponse> getCurrentDeliveryPolicy();
}
