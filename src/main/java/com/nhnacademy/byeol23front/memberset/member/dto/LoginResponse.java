package com.nhnacademy.byeol23front.memberset.member.dto;

import jakarta.servlet.http.Cookie;

public record LoginResponse(
	String accessToken,
	Cookie refreshToken
) {
}
