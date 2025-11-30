package com.nhnacademy.byeol23front.reviewset.dto;

import java.util.List;

public record ReviewRegisterRequest(
	String reviewContent,
	Byte reviewRate,
	String orderNumber,
	Long bookId,
	Boolean withImage
) {}