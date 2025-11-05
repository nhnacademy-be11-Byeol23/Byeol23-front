package com.nhnacademy.byeol23front.orderset.refundpolicy.dto;

import java.time.LocalDateTime;

public record RefundPolicyInfoResponse(String refundPolicyName,
									   String refundCondition,
									   String comment,
									   LocalDateTime changedAt) {
}
