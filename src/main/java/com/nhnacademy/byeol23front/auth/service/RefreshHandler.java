package com.nhnacademy.byeol23front.auth.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23front.auth.AuthClient;
import com.nhnacademy.byeol23front.memberset.member.dto.ReAuthenticateResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshHandler {

	private final AuthClient authClient;

	public Map<String, Collection<String>> updateHeaders(Map<String, Collection<String>> oldHeaders) {
		ReAuthenticateResponse response = authClient.reissueAccessToken();

		if (response == null || response.newAccessToken() == null || response.newAccessToken().isBlank()) {
			throw new IllegalStateException("토큰 재발급 실패");
		}

		Map<String, Collection<String>> newHeaders = new HashMap<>(oldHeaders);

		newHeaders.put("Authorization", List.of("Bearer " + response.newAccessToken()));

		return newHeaders;
	}
}
