package com.nhnacademy.byeol23front.reviewset.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record ReviewResponse(
	Long reviewId,
	String reviewerName,
	Byte reviewRate,
	String reviewContent,
	LocalDateTime revisedAt,
	List<String> reviewImageUrls
) {
}
