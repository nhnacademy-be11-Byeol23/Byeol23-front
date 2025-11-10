package com.nhnacademy.byeol23front.orderset.packaging.dto;

import java.math.BigDecimal;

public record PackagingCreateRequest(String packagingName,
									 BigDecimal packagingPrice) {

}
