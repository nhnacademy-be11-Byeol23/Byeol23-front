package com.nhnacademy.byeol23front.point.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointHistoryDTO(
	BigDecimal pointAmount,
	LocalDateTime createdAt,
	String pointPolicyName
) {
}
