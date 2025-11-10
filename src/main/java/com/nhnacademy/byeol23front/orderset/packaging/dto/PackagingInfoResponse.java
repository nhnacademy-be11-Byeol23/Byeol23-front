package com.nhnacademy.byeol23front.orderset.packaging.dto;

import java.math.BigDecimal;

public record PackagingInfoResponse(Long packagingId,
									String packagingName,
									BigDecimal packagingPrice,
									String packagingImageUrl) {
}
