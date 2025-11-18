package com.nhnacademy.byeol23front.reviewset.dto;

import java.util.List;

public record ReviewRegisterRequest(
	String reviewContent,
	Byte reviewRate,
	Long orderDetailId,
	List<String> imageUrls
) {}