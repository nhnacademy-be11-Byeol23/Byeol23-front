package com.nhnacademy.byeol23front.minio.service;

import java.util.List;

import org.aspectj.weaver.AjAttribute;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.byeol23front.minio.client.ImageFeignClient;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import com.nhnacademy.byeol23front.minio.util.MinioUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MinioService {
	private final MinioUtil minioUtil;
	private final ImageFeignClient imageFeignClient;

	public List<GetUrlResponse> getImageUrl(ImageDomain imageDomain, Long id) {
		ResponseEntity<List<GetUrlResponse>> responses = imageFeignClient.getImageUrls(imageDomain, id);
		if (responses.getBody() != null && !responses.getBody().isEmpty()) {
			return responses.getBody();
		}
		throw new RuntimeException("이미지 URL을 가져오는데 실패했습니다.");
	}

	//upload image
	public void uploadImage(ImageDomain imageDomain, Long id, MultipartFile file) {
		String imageUrl = minioUtil.putObject(imageDomain, id, file);
		imageFeignClient.uploadImage(
			new com.nhnacademy.byeol23front.minio.dto.back.ImageUploadRequest(
				id,
				imageUrl,
				imageDomain
			)
		);
	}
	public void deleteImage(ImageDomain imageDomain, Long id) {
		ResponseEntity<String> response = imageFeignClient.deleteImage(
			new com.nhnacademy.byeol23front.minio.dto.back.ImageDeleteRequest(
				id,
				imageDomain
			)
		);
		if(response.getStatusCode().is2xxSuccessful()) {
			minioUtil.deleteObject(response.getBody());
		}

	}
}
