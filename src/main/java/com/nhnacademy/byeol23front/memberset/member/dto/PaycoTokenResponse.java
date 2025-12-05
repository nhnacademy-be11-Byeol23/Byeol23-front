package com.nhnacademy.byeol23front.memberset.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaycoTokenResponse(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("access_token_secret") String secret,
	@JsonProperty("token_type") String tokenType,
	@JsonProperty("expires_in") Long expiration,
	@JsonProperty("refresh_token") String refreshToken
) {
}
