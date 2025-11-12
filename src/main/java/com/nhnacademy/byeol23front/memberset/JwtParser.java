package com.nhnacademy.byeol23front.memberset;

import java.security.PublicKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtParser {

	private final PublicKey publicKey;

	public Long jwtParseMemberId(String jwt) {
		try {
			if (jwt.startsWith("Bearer ")) {
				jwt = jwt.substring(7);
			}

			// JWT 서명 검증 + 파싱
			Jws<Claims> jws = Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build()
				.parseClaimsJws(jwt);

			Claims claims = jws.getBody();

			Object memberIdObj = claims.get("memberId");
			if (memberIdObj == null) return null;

			return Long.parseLong(memberIdObj.toString());
		} catch (JwtException e) {
			System.err.println("Invalid JWT: " + e.getMessage());
			return null;
		}
	}
}
