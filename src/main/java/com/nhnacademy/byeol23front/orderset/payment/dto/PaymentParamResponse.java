package com.nhnacademy.byeol23front.orderset.payment.dto;

public record PaymentParamResponse(String orderId,
								   String paymentKey,
								   int amount) {
}
