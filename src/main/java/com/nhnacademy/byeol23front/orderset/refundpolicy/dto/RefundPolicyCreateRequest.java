package com.nhnacademy.byeol23front.orderset.refundpolicy.dto;

public record RefundPolicyCreateRequest(String refundPolicyName,
										String refundCondition,
										String comment) {

}
