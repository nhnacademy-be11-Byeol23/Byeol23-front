package com.nhnacademy.byeol23front.point.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointPolicyDTO(
	String pointPolicyName,
	BigDecimal saveAmount,
	Boolean isActive,
	LocalDateTime createdAt
) {
	public PointPolicyDTO(String pointPolicyName, BigDecimal saveAmount, Boolean isActive, LocalDateTime createdAt) {
		this.pointPolicyName = pointPolicyName;
		this.saveAmount = saveAmount;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}
}