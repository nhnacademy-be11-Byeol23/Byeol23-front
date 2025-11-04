package com.nhnacademy.byeol23front.orderset.refundpolicy.dto;

import java.time.LocalDateTime;

public record RefundPolicyCreateResponse(Long refundPolicyId,
										 String refundPolicyName,
										 String refundCondition,
										 String comment,
										 LocalDateTime changedAt) {
}
