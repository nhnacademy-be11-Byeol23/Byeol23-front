package com.nhnacademy.byeol23front.orderset.packaging.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

public record PackagingCreateResponse(Long packagingId,
									  String packagingName,
									  BigDecimal packagingPrice,
									  MultipartFile packagingImgUrl) {
}
