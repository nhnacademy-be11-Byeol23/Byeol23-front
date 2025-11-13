package com.nhnacademy.byeol23front.bookset.bookAladin.dto;

import java.util.Date;

public record AladinItem(
	String title,
	String author,
	String publisher,
	String description,
	int priceStandard,
	int priceSales,
	String isbn13,
	String stockStatus,
	Date pubDate
) {
}