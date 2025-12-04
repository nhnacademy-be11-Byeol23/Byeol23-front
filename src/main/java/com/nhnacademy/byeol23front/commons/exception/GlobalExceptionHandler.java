package com.nhnacademy.byeol23front.commons.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginFailureException.class)
    public void handleLoginFailure(LoginFailureException e,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {

        log.warn("LoginFailureException 발생: {}", e.getMessage());

        String message = e.getMessage() == null ? "" : e.getMessage();
        String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8);

        // /members/login?loginFailed=true&errorMsg=...
        String redirectUrl = "/members/login?loginFailed=true&errorMsg=" + encodedMsg;
        response.sendRedirect(redirectUrl);
    }

    /**
     * 토큰 만료 등으로 던지는 ExpiredTokenException도
     * 최종적으로 로그인 화면으로 보내고 싶다면 같이 처리할 수 있습니다.
     */
    @ExceptionHandler(ExpiredTokenException.class)
    public void handleExpiredToken(ExpiredTokenException e,
                                   HttpServletResponse response) throws IOException {
        log.warn("ExpiredTokenException 발생: {}", e.getMessage());
        String encodedMsg = URLEncoder.encode("다시 로그인해 주세요.", StandardCharsets.UTF_8);
        response.sendRedirect("/members/login?loginFailed=true&errorMsg=" + encodedMsg);
    }
}
