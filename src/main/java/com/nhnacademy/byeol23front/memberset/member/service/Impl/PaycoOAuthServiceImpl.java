package com.nhnacademy.byeol23front.memberset.member.service.Impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nhnacademy.byeol23front.memberset.member.dto.PaycoTokenResponse;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoUserInfo;
import com.nhnacademy.byeol23front.memberset.member.dto.PaycoUserInfoResponse;
import com.nhnacademy.byeol23front.memberset.member.service.PaycoOAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaycoOAuthServiceImpl implements PaycoOAuthService {

	@Value("${payco.client-id}")
	private String clientId;
	@Value("${payco.client-secret}")
	private String clientSecret;
	@Value("${payco.redirect-uri}")
	private String redirectURI;
	@Value("${payco.authorize-uri}")
	private String authorizeURI;
	@Value("${payco.token-uri}")
	private String tokenURI;
	@Value("${payco.user-info-uri}")
	private String userInfoURI;
	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public String generateState() {
		return UUID.randomUUID().toString();
	}

	@Override
	public String buildAuthorizeUrl(String state) {
		String redirect = URLEncoder.encode(redirectURI, StandardCharsets.UTF_8);
		return authorizeURI
			+ "?response_type=code"
			+ "&client_id=" + clientId
			+ "&serviceProviderCode=FRIENDS"
			+ "&redirect_uri=" + redirect
			+ "&state=" + state
			+ "&userLocale=ko_KR";
	}

	@Override
	public PaycoTokenResponse issueTokenFromPayco(String code) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("code", code);
		body.add("redirect_uri", redirectURI);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
		ResponseEntity<PaycoTokenResponse> response =
			restTemplate.postForEntity(tokenURI, entity, PaycoTokenResponse.class);

		return response.getBody();
	}

	@Override
	public PaycoUserInfo getUserInfo(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("client_id", clientId);
		headers.set("access_token", accessToken);

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<PaycoUserInfoResponse> response =
			restTemplate.exchange(
				userInfoURI,
				HttpMethod.POST,
				entity,
				PaycoUserInfoResponse.class
			);

		PaycoUserInfoResponse body = response.getBody();
		if (body == null || body.data() == null) {
			return null;
		}
		return body.data().member();
	}
}