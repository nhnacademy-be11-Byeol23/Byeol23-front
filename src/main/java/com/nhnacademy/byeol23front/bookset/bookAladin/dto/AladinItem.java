package com.nhnacademy.byeol23front.bookset.bookAladin.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;


public record AladinItem(
	String title,
	String author,
	String publisher,
	String description,
	int priceStandard,
	int priceSales,
	String isbn13,
	String stockStatus,
	Date pubDate,
	@JsonProperty("cover")
	String imageUrl
) {
}