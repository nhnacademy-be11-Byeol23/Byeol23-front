package com.nhnacademy.byeol23front.commons.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<AccessDeniedException> AccessDeniedHandle() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

//	@ExceptionHandler(AccessTokenExpiredException.class)
//	public String handleAccessTokenExpired(HttpServletRequest request,
//										   HttpServletResponse response) {
//
//		// 1) 여기서 다시 "브라우저 → 프론트 서버" 요청의 쿠키를 볼 수 있다.
//		Cookie[] cookies = request.getCookies();
//		String refreshToken = extractRefreshToken(cookies);
//
//		// 2) 그 refreshToken을 들고 Auth 서버의 /auth/refresh 로 Feign 호출
//		RefreshResponse refreshResponse = authClient.refresh(
//				new RefreshRequest(refreshToken)
//		);
//
//		// 3) 새 토큰을 쿠키로 세팅
//		ResponseCookie newAccessCookie  = ...
//		ResponseCookie newRefreshCookie = ...
//		response.addHeader("Set-Cookie", newAccessCookie.toString());
//		response.addHeader("Set-Cookie", newRefreshCookie.toString());

//		// 4) 그 다음 원래 가려던 URL로 redirect
//		String uri   = request.getRequestURI();
//		String query = request.getQueryString();
//		String redirectUrl = (query == null) ? uri : uri + "?" + query;
//
//		return "redirect:" + redirectUrl;
//	}

}
