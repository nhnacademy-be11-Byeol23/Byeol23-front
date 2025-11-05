package com.nhnacademy.byeol23front.orderset.delivery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeliveryPolicyCreateResponse(Long deliveryPolicyId,
										   BigDecimal freeDeliveryCondition,
										   BigDecimal deliveryFee,
										   LocalDateTime changedAt) {
}
