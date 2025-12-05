package com.nhnacademy.byeol23front.orderset.payment.dto;

import java.math.BigDecimal;

public record PaymentParamRequest(String orderId,
								  String paymentKey,
								  BigDecimal amount) {
}
