package com.nhnacademy.byeol23front.minio.dto.back;

import com.nhnacademy.byeol23front.minio.util.ImageDomain;

public record ImageUploadRequest(
	Long id,
	String imageUrl,
	ImageDomain imageDomain
) {
}
