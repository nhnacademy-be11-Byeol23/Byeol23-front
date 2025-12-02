package com.nhnacademy.byeol23front.commons.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

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
	private final MemberService memberService;
	private final long accessTokenExp;

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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String uri = request.getRequestURI();

		if (uri.startsWith("/members/login")
			|| uri.startsWith("/members/join")
			|| uri.startsWith("/css/")
			|| uri.startsWith("/js/")
			|| uri.startsWith("/images/")
			|| uri.startsWith("/assets/")) {

			filterChain.doFilter(request, response);
			return;
		}

		Boolean refreshAttempted = (Boolean)request.getAttribute("REFRESH_ATTEMPTED");
		if (Boolean.TRUE.equals(refreshAttempted)) {
			filterChain.doFilter(request, response);
			return;
		}

		Tokens tokens = getTokens(request);
		log.debug("[JWT] accessToken={}, refreshToken={}, uri={}",
			tokens.accessToken() != null ? "exists" : "null",
			tokens.refreshToken() != null ? "exists" : "null",
			request.getRequestURI());
		String accessToken = tokens.accessToken();

		if (accessToken != null) {
			try {
				Claims claims = jwtParser.parseToken(accessToken);
				Authentication authentication = createAuthentication(claims);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				log.debug("JWT authentication successful for memberId: {}", claims.get("memberId"));
			} catch (ExpiredJwtException e) {
				log.debug("Access token expired, attempting refresh");
				String refreshToken = tokens.refreshToken();
				
				if (refreshToken != null) {
					try {
						String newAccessToken = memberService.reissueAccessToken(refreshToken).newAccessToken();
						if (newAccessToken != null && !newAccessToken.isEmpty()) {
							Claims newClaims = jwtParser.parseToken(newAccessToken);
							Authentication authentication = createAuthentication(newClaims);
							SecurityContextHolder.getContext().setAuthentication(authentication);

							ResponseCookie cookie = ResponseCookie.from("Access-Token", newAccessToken)
								.httpOnly(true)
								.secure(false)
								.path("/")
								.sameSite("Lax")
								.maxAge(Duration.ofMinutes(accessTokenExp))
								.build();

							response.addHeader("Set-Cookie", cookie.toString());
							request.setAttribute("NEW_ACCESS_TOKEN", newAccessToken);
							log.debug("Token refreshed successfully");
						} else {
							log.warn("Token refresh returned null or empty");
							handleUnauthenticated(request, response);
							return;
						}
					} catch (Exception refreshException) {
						log.error("Token refresh failed", refreshException);
						handleUnauthenticated(request, response);
						return;
					}
				} else {
					log.warn("No refresh token available");
					handleUnauthenticated(request, response);
					return;
				}
			} catch (Exception e) {
				log.warn("JWT parsing failed: {}", e.getMessage());
				SecurityContextHolder.clearContext();
			}
		} else {
			log.debug("No access token found in cookies");
		}
		
		filterChain.doFilter(request, response);
	}

	private void handleUnauthenticated(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		SecurityContextHolder.clearContext();
		
		// AJAX/Fetch 요청인지 확인
		String acceptHeader = request.getHeader("Accept");
		boolean isAjaxRequest = acceptHeader != null && acceptHeader.contains("application/json");
		boolean isFetchRequest = "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) 
			|| request.getHeader("X-Request-Type") != null;
		
		// Fetch 요청의 경우 (bookForm.js에서 보내는 요청)
		if (isAjaxRequest || isFetchRequest) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setHeader("Location", "/members/login");
			response.getWriter().write("{\"error\":\"Unauthorized\",\"redirect\":\"/members/login\"}");
			return;
		}
		
		// 일반 요청의 경우 리다이렉트
		response.sendRedirect("/members/login");
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

	private record Tokens(String accessToken, String refreshToken) {}
}
