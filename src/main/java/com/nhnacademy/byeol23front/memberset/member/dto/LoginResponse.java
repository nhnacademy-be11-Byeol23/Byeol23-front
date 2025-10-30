package com.nhnacademy.byeol23front.memberset.member.dto;

public record LoginResponse(
	String nickname,
	String refreshToken,
	String accessToken
) {
}
