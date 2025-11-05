package com.nhnacademy.byeol23front.orderset.delivery.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/policies/deliveries")
@RequiredArgsConstructor
public class DeliveryPolicyController {

	private final DeliveryApiClient deliveryApiClient;

	@GetMapping
	public String getDeliveryMain(@PageableDefault(size = 10, sort = "changedAt", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
		ResponseEntity<Page<DeliveryPolicyInfoResponse>> response = deliveryApiClient.getDeliveryPolicies(pageable);

		if (!response.getStatusCode().is2xxSuccessful()) {
			String message = "배송비 정책 생성에 실패했습니다.: " + response.getStatusCode();
			model.addAttribute("status", 400); // 400 Bad Request
			model.addAttribute("error", "뀨");
			model.addAttribute("message", message);
			return "error";
		}

		Page<DeliveryPolicyInfoResponse> deliveryPage = response.getBody();

		model.addAttribute("policies", deliveryPage);
		return "admin/policy/delivery";
	}

	@PostMapping
	public String createDeliveryPolicy(DeliveryPolicyCreateRequest deliveryPolicyCreateRequest,
		Model model) {
		ResponseEntity<DeliveryPolicyCreateResponse> response = deliveryApiClient.createDeliveryPolicy(deliveryPolicyCreateRequest);

		if (!response.getStatusCode().is2xxSuccessful()) {
			String message = "배송비 정책 생성에 실패했습니다.: " + response.getStatusCode();
			model.addAttribute("status", 400); // 400 Bad Request
			model.addAttribute("error", "뀨");
			model.addAttribute("message", message);
			return "error";
		}

		return "redirect:/admin/policies/deliveries";
	}

}
