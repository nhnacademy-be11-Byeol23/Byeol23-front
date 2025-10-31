package com.nhnacademy.byeol23front.minio.dto;

import com.nhnacademy.byeol23front.minio.util.ImageType;

public record GetImageUrlRequest(
	ImageType imageType,
	Long id
) {
}
