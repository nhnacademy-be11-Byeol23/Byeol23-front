package com.nhnacademy.byeol23front.point.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointPolicyDTO(
	ReservedPolicy type,
	Long pointPolicyId,
	String pointPolicyName,
	BigDecimal saveAmount,
	Boolean isActive,
	LocalDateTime createdAt
) {
	public PointPolicyDTO(
		ReservedPolicy type,
		Long pointPolicyId,
		String pointPolicyName,
		BigDecimal saveAmount,
		Boolean isActive,
		LocalDateTime createdAt)
	{
		this.type = type;
		this.pointPolicyId = pointPolicyId;
		this.pointPolicyName = pointPolicyName;
		this.saveAmount = saveAmount;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}
}