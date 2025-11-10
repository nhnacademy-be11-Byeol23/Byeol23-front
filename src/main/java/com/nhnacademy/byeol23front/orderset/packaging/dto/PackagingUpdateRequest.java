package com.nhnacademy.byeol23front.orderset.packaging.dto;

import java.math.BigDecimal;

public record PackagingUpdateRequest(String packagingName,
									 BigDecimal packagingPrice) {
}
