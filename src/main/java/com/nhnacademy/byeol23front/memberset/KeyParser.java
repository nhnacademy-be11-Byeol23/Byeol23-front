package com.nhnacademy.byeol23front.memberset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class KeyParser {

		@Bean(name = "jwtPublicKey")
		public PublicKey jwtPublicKey(@Value("${jwt.public-key}") String publicKeyPem) throws Exception {
			// PEM 헤더/푸터 제거 + 공백 제거
			String key = publicKeyPem
				.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s+", "");

			byte[] der = Base64.getDecoder().decode(key);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
			return KeyFactory.getInstance("RSA").generatePublic(spec);
		}
}