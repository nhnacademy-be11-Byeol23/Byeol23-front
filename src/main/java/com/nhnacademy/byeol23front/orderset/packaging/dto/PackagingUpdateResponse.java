package com.nhnacademy.byeol23front.orderset.packaging.dto;

import java.math.BigDecimal;


public record PackagingUpdateResponse(String packagingName,
									  BigDecimal packagingPrice,
									  String packagingImageUrl) {

}
