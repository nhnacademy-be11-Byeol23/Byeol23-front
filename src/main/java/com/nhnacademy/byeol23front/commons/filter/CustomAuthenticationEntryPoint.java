package com.nhnacademy.byeol23front.commons.filter;

import com.nhnacademy.byeol23front.memberset.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MemberService memberService;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Boolean expired = (Boolean) request.getAttribute("ACCESS_TOKEN_EXPIRED");
        String requestUri = request.getRequestURI();
        String query = request.getQueryString();
        String originalUrl = (query == null) ? requestUri : requestUri + "?" + query;
        String refreshToken = request.getCookies() == null ? null :
                Arrays.stream(request.getCookies())
                        .filter(c -> "Refresh-Token".equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);

        // 1) Access 토큰 만료인 경우 → refresh 시도
        if (Boolean.TRUE.equals(expired)) {
            try {
                String newAccessToken = memberService.reissueAccessToken(refreshToken).newAccessToken();
                if (newAccessToken != null && !newAccessToken.isEmpty()) {

                    ResponseCookie accessCookie = ResponseCookie.from("Access-Token", newAccessToken)
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .sameSite("Lax")
                            .build();
                    response.addHeader("Set-Cookie", accessCookie.toString());
                    response.sendRedirect(originalUrl);
                    return;
                } else {

                    response
                            .sendRedirect("members/login");
                    return;
                }
            } catch (Exception e) {
                log.error("Refresh process error", e);
                response.sendRedirect("members/login");
                return;
            }
        }

        log.info("Unauthenticated request for {}, redirect to login", originalUrl);
        response.sendRedirect("/login");
    }
}
