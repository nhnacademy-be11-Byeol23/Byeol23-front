package com.nhnacademy.byeol23front.minio.service;

import java.util.List;

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

	public List<GetUrlResponse> getImageUrl(ImageDomain imageDomain, Long domainId) {
		ResponseEntity<List<GetUrlResponse>> responses = imageFeignClient.getImageUrls(imageDomain, domainId);
		if (responses.getBody() != null && !responses.getBody().isEmpty()) {
			return responses.getBody();
		}
		throw new RuntimeException("이미지 URL을 가져오는데 실패했습니다.");
	}

	// upload image : 업로드 후 생성된 URL을 반환하도록 변경
	public String uploadImage(ImageDomain imageDomain, Long domainId, MultipartFile file) {
		String imageUrl = minioUtil.putObject(imageDomain, domainId, file);
		try {
			imageFeignClient.uploadImage(
				new com.nhnacademy.byeol23front.minio.dto.back.ImageUploadRequest(
					domainId,
					imageUrl,
					imageDomain
				)
			);
		} catch (Exception e) {
			// 이미지 URL 저장에 실패한 경우, Minio에서 업로드한 이미지를 삭제
			minioUtil.deleteObject(imageUrl);
			throw new RuntimeException("이미지 URL 저장에 실패했습니다.", e);
		}
		return imageUrl;
	}
	public void deleteImage(ImageDomain imageDomain, Long imageId) {
		ResponseEntity<String> response = imageFeignClient.deleteImage(
			new com.nhnacademy.byeol23front.minio.dto.back.ImageDeleteRequest(
				imageId,
				imageDomain
			)
		);
		if(response.getStatusCode().is2xxSuccessful()) {
			minioUtil.deleteObject(response.getBody());
		}

	}
}
