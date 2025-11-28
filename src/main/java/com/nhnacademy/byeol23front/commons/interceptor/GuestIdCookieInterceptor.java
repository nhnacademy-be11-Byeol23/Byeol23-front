package com.nhnacademy.byeol23front.commons.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class GuestIdCookieInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 회원판별, 회원인 경우 쿠키를 생성하지 않음
        if(request.getCookies() != null) {
            String accessToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("Access-Token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if(StringUtils.isNotBlank(accessToken)) {
                return true;
            }
        }

        // 비회원이지만 guestId 쿠키 있는 경우 쿠키를 생성하지 않음
        if(request.getCookies() != null &&
                Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("guestId")).anyMatch(cookie -> StringUtils.isNotBlank(cookie.getValue()))) {
            return true;
        }

        String guestId = generateGuestId();
        String cookie = createCookie(guestId);
        response.addHeader("Set-Cookie", cookie);

        log.info("비회원 쿠키 생성: {}", guestId);
        return true;
    }

    private String generateGuestId() {
        return UUID.randomUUID().toString();
    }

    private String createCookie(String value) {
        return ResponseCookie.from("guestId").value(value).httpOnly(true).path("/").sameSite("Lax").maxAge(21600).build().toString();
    }
}