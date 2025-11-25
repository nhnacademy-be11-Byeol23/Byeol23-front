package com.nhnacademy.byeol23front.minio.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.byeol23front.minio.client.ImageFeignClient;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.dto.back.ImageUploadRequest;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import com.nhnacademy.byeol23front.minio.util.MinioUtil;
import com.nhnacademy.byeol23front.minio.util.file.FileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {
	private final MinioUtil minioUtil;
	private final ImageFeignClient imageFeignClient;

	public List<GetUrlResponse> getImageUrl(ImageDomain imageDomain, Long domainId) {
		ResponseEntity<List<GetUrlResponse>> responses = imageFeignClient.getImageUrls(imageDomain, domainId);
		if (responses.getBody() != null && !responses.getBody().isEmpty()) {

			List<GetUrlResponse> proxiedResponses = responses.getBody().stream()
				.map(response -> {
					try {
						String url = response.imageUrl().replace("http://storage.java21.net:8000/", "https://byeol23.shop/img-proxy");
						return new GetUrlResponse(response.imageId(), url);
					} catch (Exception e) {
						return response;
					}
				})
				.collect(Collectors.toList());
			return proxiedResponses;
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
	// upload image : 업로드 후 url을 반환하도록 변경
	public String uploadImageFromUrl(ImageDomain imageDomain, Long domainId, String url){
		log.info("URL에서 이미지 다운로드 시작: domain={}, domainId={}, url={}", imageDomain, domainId, url);
		try {
			MultipartFile imageFile = FileUtil.fromUrl(url);
			log.info("이미지 다운로드 완료: fileName={}, size={}", imageFile.getOriginalFilename(), imageFile.getSize());

			String imageUrl = minioUtil.putObject(imageDomain, domainId, imageFile);
			log.info("MinIO 업로드 완료: imageUrl={}", imageUrl);

			try{
				imageFeignClient.uploadImage(
					new ImageUploadRequest(
						domainId,
						imageUrl,
						imageDomain
					)
				);
				log.info("이미지 URL 저장 완료: domainId={}, imageUrl={}", domainId, imageUrl);
			}catch (Exception e){
				log.error("이미지 URL 저장 실패: domainId={}, imageUrl={}, error={}", domainId, imageUrl, e.getMessage(), e);
				minioUtil.deleteObject(imageUrl);
				throw new RuntimeException("이미지 URL 저장에 실패했습니다.", e);
			}
			return imageUrl;
		} catch (Exception e) {
			log.error("이미지 업로드 전체 프로세스 실패: domain={}, domainId={}, url={}, error={}",
					imageDomain, domainId, url, e.getMessage(), e);
			throw e;
		}
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
