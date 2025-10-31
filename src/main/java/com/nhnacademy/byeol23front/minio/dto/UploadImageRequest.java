package com.nhnacademy.byeol23front.minio.dto;

import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.byeol23front.minio.util.ImageType;

public record UploadImageRequest(
	ImageType imageType,
	Long id
) {
}
