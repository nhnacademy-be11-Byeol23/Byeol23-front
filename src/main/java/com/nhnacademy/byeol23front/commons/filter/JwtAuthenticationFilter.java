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

import com.nhnacademy.byeol23front.auth.MemberPrincipal;
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
					response.sendRedirect("/members/login");
					return;
				}
				String newAccessToken = tokenRefreshService.refreshTokens();

				if (newAccessToken == null || newAccessToken.isBlank()) {
					response.sendRedirect("/members/login");
					return;
				}
				Claims newClaims = jwtParser.parseToken(newAccessToken);
				Authentication authentication = createAuthentication(newClaims);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
			response.sendRedirect("/members/login");
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
