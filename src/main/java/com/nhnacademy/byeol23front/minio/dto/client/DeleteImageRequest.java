package com.nhnacademy.byeol23front.minio.dto.client;

import com.nhnacademy.byeol23front.minio.util.ImageDomain;

public record DeleteImageRequest(
	ImageDomain imageDomain,
	Long id
) {
}
