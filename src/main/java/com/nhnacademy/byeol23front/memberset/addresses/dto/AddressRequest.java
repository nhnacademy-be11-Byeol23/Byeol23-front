package com.nhnacademy.byeol23front.memberset.addresses.dto;

public record AddressRequest(Long addressId,
							 String postCode,
							 String addressInfo,
							 String addressDetail,
							 String addressExtra,
							 String addressAlias,
							 Boolean isDefault) {
}
