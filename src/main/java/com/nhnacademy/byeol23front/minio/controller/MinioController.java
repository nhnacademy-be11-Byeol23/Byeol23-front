package com.nhnacademy.byeol23front.minio.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nhnacademy.byeol23front.auth.Authorized;
import com.nhnacademy.byeol23front.auth.Role;
import com.nhnacademy.byeol23front.minio.dto.DeleteImageRequest;
import com.nhnacademy.byeol23front.minio.dto.GetImageUrlRequest;
import com.nhnacademy.byeol23front.minio.dto.UploadImageRequest;
import com.nhnacademy.byeol23front.minio.util.MinioUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/minio")
public class MinioController {
	private final MinioUtil minioUtil;

	@Authorized(role = Role.ADMIN)
	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(
		@RequestBody UploadImageRequest request
	) {
		//TODO file 검사로직
		String url = minioUtil.putObject(request.imageType(), request.id(), request.file());
		//TODO DB저장(이벤트로 처리)
		//DB저장 실패시 삭제로직
		if(false){
			try {
				minioUtil.deleteObject(request.imageType(), request.id());
			} catch (Exception e) {
				log.error("DB 저장 실패 후 이미지 삭제 실패: {}", e.getMessage());
			}
			return ResponseEntity.internalServerError().body("이미지 저장에 실패했습니다.");
		}
		return ResponseEntity.ok(url);

	}

	@GetMapping("/images")
	public ResponseEntity<List<String>> getImageRequest(
		@RequestBody GetImageUrlRequest request
	) {
		//TODO backend 통신로직 및 그대로 반환
		//임시코드
		return new ResponseEntity<>(new ArrayList<>(){{
			add("https://byeol23-minio.s3.ap-northeast-2.amazonaws.com/book/1/1679495604869-1659136349934-1659136349934.png");
			add("https://byeol23-minio.s3.ap-northeast-2.amazonaws.com/book/1/1679495604870-1659136349934-1659136349934.png");
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
