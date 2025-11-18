package com.nhnacademy.byeol23front.commons.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nhnacademy.byeol23front.commons.parser.JwtParser;

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


	public Authentication createAuthentication(Claims claims) {
		String role = claims.get("role", String.class);
		Long memberId = claims.get("memberId", Long.class);
		List<GrantedAuthority> authorities =
			List.of(new SimpleGrantedAuthority(role));

		UserDetails principal = User.withUsername(memberId.toString())
			.password("")
			.authorities(authorities)
			.build();

		return new UsernamePasswordAuthenticationToken(
			principal,
			null,
			authorities
		);
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String uri = request.getRequestURI();

		String token = getToken(request);

		if(token != null) {
			try {
				Claims claims = jwtParser.parseToken(token);
				Authentication authentication = createAuthentication(claims);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				SecurityContextHolder.clearContext();
			}
		}
		filterChain.doFilter(request, response);
	}

	private String getToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies) {
			if("Access-Token".equals(cookie.getName())) {
				return cookie.getValue().startsWith("Bearer") ? cookie.getValue().substring(7) : cookie.getValue();
			}
		}
		return null;
	}
}
