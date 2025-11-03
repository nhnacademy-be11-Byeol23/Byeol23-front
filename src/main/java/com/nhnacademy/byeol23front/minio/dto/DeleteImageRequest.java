package com.nhnacademy.byeol23front.minio.dto;

import com.nhnacademy.byeol23front.minio.util.ImageType;

public record DeleteImageRequest(
	ImageType imageType,
	Long id
) {
}
