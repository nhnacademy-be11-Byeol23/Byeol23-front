// src/main/java/com/nhnacademy/byeol23front/reviewset/controller/ReviewController.java

package com.nhnacademy.byeol23front.reviewset.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import com.nhnacademy.byeol23front.reviewset.client.ReviewFeignClient;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController {
	private final MinioService minioService;
	private final ReviewFeignClient reviewFeignClient;

	@PostMapping("/reviews")
	public ResponseEntity<Void> registerReview(
		@RequestParam(value = "image", required = false) MultipartFile image,
		@RequestParam("reviewContent") String reviewContent,
		@RequestParam("reviewRate") Byte reviewRate,
		@RequestParam("orderDetailId") Long orderDetailId
	) {
		Long id = reviewFeignClient.registerReview(
			new com.nhnacademy.byeol23front.reviewset.dto.ReviewRegisterRequest(
				reviewContent,
				reviewRate,
				orderDetailId,
				null
			)
		).getBody();

		if (image != null && !image.isEmpty() && id != null) {
			minioService.uploadImage(ImageDomain.REVIEW, id, image);
		}

		return ResponseEntity.ok().build();
	}
}
