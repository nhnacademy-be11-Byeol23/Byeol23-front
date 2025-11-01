package com.nhnacademy.byeol23front.orderset.delivery.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;

@FeignClient(name = "deliveryApiClient", url = "${backend.api.url}")
public interface DeliveryApiClient {

	@GetMapping("/api/delivery-policies")
	ResponseEntity<List<DeliveryPolicyInfoResponse>> getDeliveryPolicies();

	@PostMapping("/api/delivery-policies")
	ResponseEntity<DeliveryPolicyCreateResponse> createDeliveryPolicy(DeliveryPolicyCreateRequest deliveryPolicyCreateRequest);

	@GetMapping("/api/delivery-policies/current")
	ResponseEntity<DeliveryPolicyInfoResponse> getCurrentDeliveryPolicy();
}
