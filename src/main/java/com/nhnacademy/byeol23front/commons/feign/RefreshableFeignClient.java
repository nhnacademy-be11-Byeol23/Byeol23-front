package com.nhnacademy.byeol23front.commons.feign;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.nhnacademy.byeol23front.auth.service.RefreshHandler;

import feign.Client;
import feign.Request;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RefreshableFeignClient implements Client {

	private final Client delegate;
	private final RefreshHandler refreshHandler;
	@Override
	public Response execute(Request request, Request.Options options) throws IOException {
		String url = request.url();
		Map<String, Collection<String>> headers = request.headers();
		Collection<String> refreshHeaderValues = headers.get("Refresh-Token");

		String refreshToken = null;
		if (refreshHeaderValues != null && !refreshHeaderValues.isEmpty()) {
			refreshToken = refreshHeaderValues.iterator().next();
		}

		Response response = delegate.execute(request, options);

		if (url.contains("/auth/refresh")) {
			return response;
		}

		if (response.status() == 401) {
			log.warn("AccessToken expired → Refresh 시도");

			if (refreshToken == null) {
				log.error("Refresh 실패 → 401 그대로 반환");
				return response;
			}

			Request newRequest = Request.create(
				request.httpMethod(),
				request.url(),
				refreshHandler.updateHeaders(request.headers()),
				request.body(),
				request.charset(),
				request.requestTemplate()
			);

			log.info("토큰 재발급 성공 → 원 요청 재전송");
			return delegate.execute(newRequest, options);
		}

		return response;
	}
}
