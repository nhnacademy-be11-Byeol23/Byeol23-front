package com.nhnacademy.byeol23front.commons.parser;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23front.commons.exception.KeyLoadFailureException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtParser {

	private final PublicKey publicKey;

	public JwtParser(@Value("${jwt.public-key}") String pem) {
		this.publicKey = parse(pem);
	}

	public Claims parseToken(String token) {
		return Jwts.parser()
			.verifyWith(publicKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private PublicKey parse(String pem) {
		try {
			String base64 = pem.replaceAll("-----BEGIN (.*)-----", "")
				.replaceAll("-----END (.*)-----", "")
				.replaceAll("\\s", "");
			byte[] der = Base64.getDecoder().decode(base64);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
			return KeyFactory.getInstance("RSA").generatePublic(spec);
		} catch (Exception e) {
			throw new KeyLoadFailureException("PublicKey 로딩 실패: " + e.getMessage());
		}
	}
}
