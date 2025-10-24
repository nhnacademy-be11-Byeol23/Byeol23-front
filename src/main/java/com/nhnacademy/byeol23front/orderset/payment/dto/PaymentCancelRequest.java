package com.nhnacademy.byeol23front.orderset.payment.dto;

public record PaymentCancelRequest(String cancelReason,
								   String paymentKey) {
}
