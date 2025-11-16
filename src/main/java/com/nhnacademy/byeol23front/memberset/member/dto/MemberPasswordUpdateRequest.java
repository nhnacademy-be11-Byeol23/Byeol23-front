package com.nhnacademy.byeol23front.memberset.member.dto;

public record MemberPasswordUpdateRequest(
	String loginId,
	String currentPassword,
	String newPassword
) {
}
