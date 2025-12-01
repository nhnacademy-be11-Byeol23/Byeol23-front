package com.nhnacademy.byeol23front.memberset.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaycoUserInfo(
	@JsonProperty("idNo") String paycoId,
	@JsonProperty("email") String email,
	@JsonProperty("mobile") String phoneNumber,
	@JsonProperty("name") String name,
	@JsonProperty("birthdayMMdd") String birthdate
) {}
