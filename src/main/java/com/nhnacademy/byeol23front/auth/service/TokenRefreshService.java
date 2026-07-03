package com.nhnacademy.byeol23front.auth.service;

import com.nhnacademy.byeol23front.auth.CookieProperties;
import com.nhnacademy.byeol23front.auth.feign.AuthClient;
import com.nhnacademy.byeol23front.auth.feign.TokenContext;
import com.nhnacademy.byeol23front.commons.exception.ExpiredRefreshTokenException;
import com.nhnacademy.byeol23front.memberset.member.dto.ReAuthenticateResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final AuthClient authClient;
    private final CookieProperties cookieProperties;

    /**
     * @return 재발급 성공 여부
     */
    public String refreshTokens() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            log.error("RequestAttributes 없음 → Refresh 불가");
            return null;
        }

        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        if (request == null) {
            log.error("HttpServletRequest 없음 → Refresh 불가");
            return null;
        }

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Refresh-Token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            log.error("Refresh-Token 쿠키 없음 → 재발급 불가");
            return null;
        }

        try {
            ReAuthenticateResponse re = authClient.reissueAccessToken(); // 필요하면 refreshToken 인자로

            if (re == null || re.newAccessToken() == null || re.newAccessToken().isBlank()) {
                log.error("재발급 응답이 비어 있음");
                return null;
            }

            String newAccessToken = re.newAccessToken();

            TokenContext.set(newAccessToken);

            if (response != null) {
                // 로그인 시 발급 쿠키와 동일한 보안 속성으로 재설정한다.
                // secure/sameSite를 환경 설정(CookieProperties)에 맞춰야 dev(HTTP)에서도 브라우저가 쿠키를 저장한다.
                ResponseCookie accessCookie = ResponseCookie.from("Access-Token", newAccessToken)
                        .httpOnly(true)
                        .secure(cookieProperties.isSecure())
                        .sameSite(cookieProperties.getSameSite())
                        .path("/")
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            }

            log.info("토큰 재발급 성공. 새 AccessToken 설정 완료");
            return newAccessToken;
        } catch (ExpiredRefreshTokenException e) {
            log.error("리프레시 토큰 만료", e);
            throw e; // 다시 던지기
        } catch (Exception e) {
            log.error("토큰 재발급 중 예외 발생", e);
            return null;
        }
    }
}