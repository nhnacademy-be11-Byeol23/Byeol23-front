package com.nhnacademy.byeol23front.orderset.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResultResponse(String paymentKey,
									String orderId,
									String orderName,
									String status,
									BigDecimal totalAmount,
									LocalDateTime paymentRequestedAt,
									LocalDateTime paymentApprovedAt,
									String method) {
}
