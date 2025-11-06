package com.nhnacademy.byeol23front.orderset.refundpolicy.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhnacademy.byeol23front.orderset.refundpolicy.client.RefundPolicyApiClient;
import com.nhnacademy.byeol23front.orderset.refundpolicy.dto.RefundPolicyCreateRequest;
import com.nhnacademy.byeol23front.orderset.refundpolicy.dto.RefundPolicyCreateResponse;
import com.nhnacademy.byeol23front.orderset.refundpolicy.dto.RefundPolicyInfoResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/policies/refunds")
@RequiredArgsConstructor
public class RefundPolicyController {
	private final RefundPolicyApiClient refundPolicyApiClient;

	@GetMapping
	public String refundPolicyMain(Model model,
		@PageableDefault(size = 10, sort = "changedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		ResponseEntity<Page<RefundPolicyInfoResponse>> response = refundPolicyApiClient.getAllRefundPolicies(pageable);

		if (!response.getStatusCode().is2xxSuccessful()) {
			String message = "환불 정책 생성에 실패했습니다.: " + response.getStatusCode();
			model.addAttribute("status", 400); // 400 Bad Request
			model.addAttribute("error", "API 호출 실패");
			model.addAttribute("message", message);
			return "error";
		}

		model.addAttribute("policies", response.getBody());
		return "admin/policy/refund";
	}

	@PostMapping
	public String createRefundPolicy(@ModelAttribute RefundPolicyCreateRequest refundPolicyCreateRequest, Model model) {
		ResponseEntity<RefundPolicyCreateResponse> response = refundPolicyApiClient.createRefundPolicy(
			refundPolicyCreateRequest);

		if (!response.getStatusCode().is2xxSuccessful()) {
			String message = "환불 정책 생성에 실패했습니다.: " + response.getStatusCode();
			model.addAttribute("status", 400); // 400 Bad Request
			model.addAttribute("error", "환불 정책 생성 실패");
			model.addAttribute("message", message);
			return "error";
		}

		return "redirect:/admin/policies/refunds";
	}

}
