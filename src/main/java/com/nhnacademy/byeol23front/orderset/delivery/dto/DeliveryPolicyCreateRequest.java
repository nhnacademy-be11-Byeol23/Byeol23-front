package com.nhnacademy.byeol23front.orderset.delivery.dto;

import java.math.BigDecimal;

public record DeliveryPolicyCreateRequest(BigDecimal freeDeliveryCondition,
										  BigDecimal deliveryFee) {
}
