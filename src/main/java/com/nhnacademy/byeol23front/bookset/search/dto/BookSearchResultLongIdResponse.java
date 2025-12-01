package com.nhnacademy.byeol23front.bookset.search.dto;

import java.time.LocalDate;
import java.util.List;

public record BookSearchResultLongIdResponse(
	Long id,
	String imageUrl,
	String title,
	List<String> author,
	String publisher,
	LocalDate publishedAt,
	int regularPrice,
	int salePrice,
	int reviewCount,
	float ratingAverage,
	boolean isSoldOut
) {
	public static BookSearchResultLongIdResponse from(BookSearchResultResponse src) {
		return new BookSearchResultLongIdResponse(
			Long.valueOf(src.id()),
			src.imageUrl(),
			src.title(),
			src.author(),
			src.publisher(),
			src.publishedAt(),
			src.regularPrice(),
			src.salePrice(),
			src.reviewCount(),
			src.ratingAverage(),
			src.isSoldOut()
		);
	}
	public static List<BookSearchResultLongIdResponse> fromList(List<BookSearchResultResponse> list) {
		return list.stream()
			.map(BookSearchResultLongIdResponse::from)
			.toList();
	}
}
