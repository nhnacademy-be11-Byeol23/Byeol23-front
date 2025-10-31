package com.nhnacademy.byeol23front.minio.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.nhnacademy.byeol23front.minio.dto.DeleteImageRequest;
import com.nhnacademy.byeol23front.minio.dto.GetImageUrlRequest;
import com.nhnacademy.byeol23front.minio.dto.UploadImageRequest;
import com.nhnacademy.byeol23front.minio.util.MinioUtil;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/minio")
public class MinioController {
	private final MinioUtil minioUtil;
	private final ObjectMapper jsonMapper;

	@Authorized(role = Role.ADMIN)
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadImage(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "이미지 업로드 요청"+ """
				{
							"imageType": "BOOK",
							"id": 1
						}
				""",
			required = true,
			content = @Content(
				examples = @ExampleObject(
					name = "기본 요청 예시",
					value = """
						{
							"imageType": "BOOK",
							"id": 1
						}
						"""
				)
			)
		)
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
		//TODO file 검사로직
		String url = minioUtil.putObject(uploadImageRequest.imageType(), uploadImageRequest.id(), file);
		//TODO DB저장(이벤트로 처리)
		//DB저장 실패시 삭제로직
		if(false){
			try {
				minioUtil.deleteObject(uploadImageRequest.imageType(), uploadImageRequest.id());
			} catch (Exception e) {
				log.error("DB 저장 실패 후 이미지 삭제 실패: {}", e.getMessage());
			}
			return ResponseEntity.internalServerError().body("이미지 저장에 실패했습니다.");
		}
		return ResponseEntity.ok(url);

	}

	@GetMapping("/images/{imageType}/{id}")
	public ResponseEntity<List<String>> getImageRequest(
		@PathVariable String imageType,
		@PathVariable Long id
	){
		//TODO backend 통신로직 및 그대로 반환
		//임시코드
		return new ResponseEntity<>(new ArrayList<>(){{
			add("URL1");
			add("URL2");
		}}, null, 200
		);
	}

	@Authorized(role = Role.ADMIN)
	@PostMapping("/delete")
	public ResponseEntity<Void> deleteImage(
		@RequestBody DeleteImageRequest request
	) {
		minioUtil.deleteObject(request.imageType(), request.id());
		return ResponseEntity.ok().build();
	}
}
