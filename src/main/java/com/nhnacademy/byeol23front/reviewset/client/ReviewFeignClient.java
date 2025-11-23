package com.nhnacademy.byeol23front.reviewset.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.reviewset.dto.ReviewRegisterRequest;
import com.nhnacademy.byeol23front.reviewset.dto.ReviewResponse;

@FeignClient(name = "BYEOL23-BACKEND", contextId = "reviewFeignClient", path = "/api/reviews")
public interface ReviewFeignClient {
	@GetMapping("/product/{book-id}")
	ResponseEntity<List<ReviewResponse>> getReviewsByProductId(@PathVariable("book-id") Long bookId);

	@PostMapping
	ResponseEntity<Long> registerReview(@RequestBody ReviewRegisterRequest request);
}
