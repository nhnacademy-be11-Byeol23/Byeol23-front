package com.nhnacademy.byeol23front.reviewset.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;

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
	public ReviewResponse(
		Long reviewId,
		String reviewerName,
		Byte reviewRate,
		String reviewContent,
		LocalDateTime revisedAt,
		List<String> reviewImageUrls
	) {
		this.reviewId = reviewId;
		this.reviewerName = reviewerName;
		this.reviewRate = reviewRate;
		this.reviewContent = reviewContent;
		this.revisedAt = revisedAt;
		this.reviewImageUrls = reviewImageUrls.stream()
			.map(url -> url.replace("http://storage.java21.net:8000/", "https://byeol23.shop/img-proxy/")).toList();
	}
}
