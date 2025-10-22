package com.nhnacademy.byeol23front.orderset.order.dto;

public record PaymentParamResponse(String orderId,
								   String paymentKey,
								   int amount) {
}
