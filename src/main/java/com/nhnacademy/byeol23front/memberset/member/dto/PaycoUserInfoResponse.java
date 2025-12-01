package com.nhnacademy.byeol23front.memberset.member.dto;

public record PaycoUserInfoResponse(
	Header header,
	Data data
) {
	public record Header(
		boolean isSuccessful,
		int resultCode,
		String resultMessage
	) {}

	public record Data(
		PaycoUserInfo member
	) {}
}