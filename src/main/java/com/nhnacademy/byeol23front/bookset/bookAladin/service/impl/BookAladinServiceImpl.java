package com.nhnacademy.byeol23front.bookset.bookAladin.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nhnacademy.byeol23front.bookset.bookAladin.dto.AladinItem;
import com.nhnacademy.byeol23front.bookset.bookAladin.dto.AladinItemSearchResponse;
import com.nhnacademy.byeol23front.bookset.bookAladin.dto.AladinResult;
import com.nhnacademy.byeol23front.bookset.bookAladin.service.BookAladinService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BookAladinServiceImpl implements BookAladinService {

	private final WebClient webClient;


	@Value("${aladin.ttb-key}")
	private String ttbKey;

	@Value("${aladin.base-url}")
	private String baseUrl;


	@Override
	public AladinResult getAllBooks(String keyword, int page, int size) throws JsonProcessingException {
		int start = (page - 1) * size + 1;
		String q = UriUtils.encodeQueryParam(keyword, StandardCharsets.UTF_8);
		String body = webClient.get()
			.uri(uri -> UriComponentsBuilder.fromHttpUrl(baseUrl)
				.queryParam("ttbkey", ttbKey)
				.queryParam("QueryType", "Keyword")
				.queryParam("Query", q)
				.queryParam("Start", start)
				.queryParam("MaxResults", size)
				.queryParam("Output", "js")
				.build(true).toUri())
			.accept(MediaType.ALL)
			.retrieve().bodyToMono(String.class).block();

		ObjectMapper objectMapper = JsonMapper.builder()
			.enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER) // accepts \'
			.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)                     // accepts '...'
			.build().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		AladinItemSearchResponse resp = objectMapper.readValue(body, AladinItemSearchResponse.class);

		int total = resp.totalResults() != null ? resp.totalResults() : 0;
		int lastPage = (int)Math.ceil((double) total / size);
		return new AladinResult(keyword, page, size, lastPage, total, resp.item());
	}
}
