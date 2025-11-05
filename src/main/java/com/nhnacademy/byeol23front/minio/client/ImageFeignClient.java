package com.nhnacademy.byeol23front.minio.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.dto.back.ImageDeleteRequest;
import com.nhnacademy.byeol23front.minio.dto.back.ImageUploadRequest;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;

@FeignClient(name = "BYEOL23-BACKEND", contextId = "imageFeignClient")
public interface ImageFeignClient {

	/**
	 * POST /api/images/upload
	 * 이미지를 업로드하고 URL을 저장합니다.
	 */
	@PostMapping("/api/images/upload")
	ResponseEntity<String> uploadImage(
		@RequestBody ImageUploadRequest imageUploadRequest
	);

	/**
	 * POST /api/images/delete
	 * 이미지 ID와 도메인에 해당하는 URL을 삭제합니다.
	 */
	@PostMapping("/api/images/delete")
	ResponseEntity<String> deleteImage(
		@RequestBody ImageDeleteRequest imageDeleteRequest
	);

	/**
	 * GET /api/images/urls/{domain}/{id}
	 * 이미지 ID와 도메인에 해당하는 이미지 URL 목록을 조회합니다.
	 */
	@GetMapping("/api/images/urls/{domain}/{id}")
	ResponseEntity<List<GetUrlResponse>> getImageUrls(
		@PathVariable("domain") ImageDomain domain, // (2)
		@PathVariable("id") Long id
	);
}