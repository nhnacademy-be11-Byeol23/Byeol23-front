package com.nhnacademy.byeol23front.memberset;

import java.security.PublicKey;

import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23front.memberset.exception.DecodeingFailureException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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

			Jws<Claims> jws = Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build()
				.parseClaimsJws(jwt);

			Claims claims = jws.getBody();

			Object memberIdObj = claims.get("memberId");
			if (memberIdObj == null) {
				throw new DecodeingFailureException("memberID 필드가 존재하지 않습니다.");
			}

			return Long.parseLong(memberIdObj.toString());
		} catch (DecodeingFailureException e) {
			throw new DecodeingFailureException("RefreshToken 검증 실패: " + e.getMessage());
		}
	}
}
