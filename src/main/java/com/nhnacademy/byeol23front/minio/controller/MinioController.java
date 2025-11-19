package com.nhnacademy.byeol23front.minio.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.auth.Authorized;
import com.nhnacademy.byeol23front.auth.Role;
import com.nhnacademy.byeol23front.minio.dto.client.DeleteImageRequest;
import com.nhnacademy.byeol23front.minio.dto.client.UploadImageRequest;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/minio")
public class MinioController {
	private final MinioService minioService;
	private final ObjectMapper jsonMapper;

	@Authorized(role = Role.ADMIN)
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "이미지 업로드 요청"+ """
			{
						"imageDomain": "BOOK",
						"id": 1
					}
			""",
		required = true,
		content = @Content(
			examples = @ExampleObject(
				name = "기본 요청 예시",
				value = """
					{
						"imageDomain": "BOOK",
						"id": 1
					}
					"""
			)
		)
	)
	public ResponseEntity<String> uploadImage(
		@RequestPart("request") String request,
		@RequestPart("file") MultipartFile file
	) {
		UploadImageRequest uploadImageRequest;
		try {
			uploadImageRequest = jsonMapper.readValue(request, UploadImageRequest.class);
		} catch (Exception e) {
			log.error("Request 파싱 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().body("잘못된 요청입니다.");
		}
		// TODO: file 검사로직 (확장자/크기 등)
		String url;
		try {
			url = minioService.uploadImage(
				uploadImageRequest.imageDomain(),
				uploadImageRequest.id(),
				file
			);
		} catch (Exception e) {
			log.error("이미지 업로드 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().body("이미지 저장에 실패했습니다.");
		}
		return ResponseEntity.ok(url);
	}

	@Operation(summary = "이미지 URL 조회", description = "이미지 도메인 타입과 도메인 ID로 이미지 URL 목록을 조회합니다.")
	@GetMapping("/images/{imageType}/{id}")
	public ResponseEntity<List<GetUrlResponse>> getImageRequest(
		@Parameter(description = "이미지 도메인 타입 (예: BOOK)") @PathVariable String imageType,
		@Parameter(description = "도메인 아이디") @PathVariable Long id
	){
		List<GetUrlResponse> responses = minioService.getImageUrl(ImageDomain.valueOf(imageType), id);
		List<GetUrlResponse> proxiedResponses = responses.stream()
			.map(response -> {
				try {
					// 원본 URL 파싱 (예: http://minio:9000/bucket/file.jpg)
					URI uri = URI.create(response.imageUrl());
					String path = uri.getPath(); // 결과: "/bucket/file.jpg"

					// 프록시 URL로 재조립
					// 결과 예시: "https://your-domain.com/img-proxy/bucket/file.jpg"
					// (프론트엔드와 같은 도메인을 쓴다면 도메인 생략하고 "/img-proxy" + path 만 써도 됩니다)
					String newUrl = "https://byeol23.shop" + path;

					// 새 record 생성하여 반환
					return new GetUrlResponse(response.imageId(), newUrl);
				} catch (Exception e) {
					// URL 파싱 실패 시 원본 유지 혹은 예외 처리
					return response;
				}
			})
			.collect(Collectors.toList());

		// 3. 변환된 리스트 반환
		return ResponseEntity.ok(proxiedResponses);
	}

	@Authorized(role = Role.ADMIN)
	@Operation(summary = "이미지 삭제", description = "이미지 ID와 도메인 정보를 보내면 해당 이미지를 삭제합니다.")
	@PostMapping("/delete")
	public ResponseEntity<Void> deleteImage(
		@RequestBody DeleteImageRequest request
	) {
		minioService.deleteImage(
			request.imageDomain(),
			request.id()
		);
		return ResponseEntity.ok().build();
	}
}
