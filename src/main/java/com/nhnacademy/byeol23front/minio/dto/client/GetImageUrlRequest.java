package com.nhnacademy.byeol23front.minio.dto.client;

import com.nhnacademy.byeol23front.minio.util.ImageDomain;

public record GetImageUrlRequest(
	ImageDomain imageDomain,
	Long id
) {
}
