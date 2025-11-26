package com.nhnacademy.byeol23front.orderset.refund.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhnacademy.byeol23front.orderset.refund.client.RefundApiClient;
import com.nhnacademy.byeol23front.orderset.refund.dto.RefundRequest;
import com.nhnacademy.byeol23front.orderset.refund.dto.RefundResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/refunds")
public class RefundController {
	private final RefundApiClient refundApiClient;

	@PostMapping
	@ResponseBody
	public ResponseEntity<RefundResponse> refund(@RequestBody RefundRequest refundRequest) {
		return refundApiClient.refund(refundRequest);
	}


}
