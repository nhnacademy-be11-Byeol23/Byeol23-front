package com.nhnacademy.byeol23front.memberset.addresses.dto;

public record AddressResponse(Long addressId,
							  String postCode,
							  String addressInfo,
							  String addressDetail,
							  String addressExtra,
							  String addressAlias,
							  Boolean isDefault) {
}
