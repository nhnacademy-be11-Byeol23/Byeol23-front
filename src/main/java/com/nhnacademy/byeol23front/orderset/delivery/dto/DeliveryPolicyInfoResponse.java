package com.nhnacademy.byeol23front.orderset.delivery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeliveryPolicyInfoResponse(BigDecimal freeDeliveryCondition,
										 BigDecimal deliveryFee,
										 LocalDateTime changedAt) {
}
