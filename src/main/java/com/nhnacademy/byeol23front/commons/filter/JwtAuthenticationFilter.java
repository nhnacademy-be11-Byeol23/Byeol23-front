package com.nhnacademy.byeol23front.commons.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import com.nhnacademy.byeol23front.auth.service.TokenRefreshService;
import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nhnacademy.byeol23front.auth.CookieProperties;
import com.nhnacademy.byeol23front.auth.MemberPrincipal;
import com.nhnacademy.byeol23front.commons.exception.ExpiredRefreshTokenException;
import com.nhnacademy.byeol23front.commons.parser.JwtParser;
import com.nhnacademy.byeol23front.memberset.member.service.MemberService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtParser jwtParser;
	private final TokenRefreshService tokenRefreshService;
	private final CookieProperties cookieProperties;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String uri = request.getRequestURI();

		if (uri.startsWith("/members/login")
			|| uri.startsWith("/payco/login")
			|| uri.startsWith("/members/join")
			|| uri.startsWith("/css/")
			|| uri.startsWith("/js/")
			|| uri.startsWith("/images/")
			|| uri.startsWith("/assets/")
		) {

			filterChain.doFilter(request, response);
			return;
		}

		Tokens tokens = getTokens(request);
		String accessToken = tokens.accessToken();

		if (accessToken != null) {
			try {
				Claims claims = jwtParser.parseToken(accessToken);
				Authentication authentication = createAuthentication(claims);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (ExpiredJwtException e) {
				String refreshToken = tokens.refreshToken();

				if (refreshToken == null || refreshToken.isBlank()) {
					// 모든 쿠키 삭제
					deleteAllCookies(request, response);
					response.sendRedirect("/");
					return;
				}

				try {
					String newAccessToken = tokenRefreshService.refreshTokens();

					if (newAccessToken == null || newAccessToken.isBlank()) {
						response.sendRedirect("/members/login");
						return;
					}
					Claims newClaims = jwtParser.parseToken(newAccessToken);
					Authentication authentication = createAuthentication(newClaims);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} catch (ExpiredRefreshTokenException e2) {
					log.warn("ExpiredRefreshTokenException 발생: 리프레시 토큰 만료로 인한 로그아웃 처리");
					
					// 모든 쿠키 삭제
					deleteAllCookies(request, response);
					
					response.sendRedirect("/");
					return;
				}
			} catch (Exception e) {
				log.error("토큰 처리 중 예외 발생", e);
				// 모든 쿠키 삭제
				deleteAllCookies(request, response);
				response.sendRedirect("/");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private Tokens getTokens(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String accessToken = null;
		String refreshToken = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("Access-Token".equals(cookie.getName())) {
					String value = cookie.getValue();
					accessToken = value.startsWith("Bearer") ? value.substring(7) : value;
				}
				if ("Refresh-Token".equals(cookie.getName())) {
					refreshToken = cookie.getValue();
				}
			}
		}
		return new Tokens(accessToken, refreshToken);
	}

	private void deleteAllCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// Access-Token과 Refresh-Token만 삭제 (guestId는 유지)
				if ("Access-Token".equals(cookie.getName()) || "Refresh-Token".equals(cookie.getName()) || "PAYCO_STATE".equals(cookie.getName())) {
					ResponseCookie deleteCookie = ResponseCookie.from(cookie.getName(), "")
						.path("/")
						.httpOnly(true)
						.secure(cookieProperties.isSecure())
						.sameSite(cookieProperties.getSameSite())
						.maxAge(0)
						.build();
					response.addHeader("Set-Cookie", deleteCookie.toString());
				}
			}
		}
	}

	public Authentication createAuthentication(Claims claims) {
		String role = claims.get("role", String.class);
		Long memberId = claims.get("memberId", Long.class);
		String nickname = claims.get("nickname", String.class);
		List<GrantedAuthority> authorities =
				List.of(new SimpleGrantedAuthority(role));

		MemberPrincipal principal = new MemberPrincipal(memberId, nickname, role, authorities);

		return new UsernamePasswordAuthenticationToken(
				principal,
				null,
				principal.getAuthorities()
		);
	}
}
