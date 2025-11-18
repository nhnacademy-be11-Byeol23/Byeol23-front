package com.nhnacademy.byeol23front.bookset.bookAladin.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinItemSearchResponse(
	@JsonProperty("item")
	List<AladinItem> item,
	Integer totalResults,
	Integer startIndex,
	Integer itemsPerPage
) {
}
