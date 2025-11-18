package com.nhnacademy.byeol23front.bookset.bookAladin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookAladinCreateRequest(
	String bookName,
	String author,
	String translator,
	String publisher,
	String description,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	String isbn,
	LocalDate publishDate,
	String imageUrl,

	String toc,
	Integer stock,
	boolean isPack,
	String bookStatus,

	List<Long> categoryIds,
	List<Long> tagIds,
	List<Long> contributorIds  // 기여자 ID 목록 (author 파싱 후 생성)
) {
	public BookAladinCreateRequest {
		if (categoryIds == null) {
			categoryIds = List.of();
		}
		if (tagIds == null) {
			tagIds = List.of();
		}
		if (contributorIds == null) {
			contributorIds = List.of();
		}
		if (bookStatus == null || bookStatus.isBlank()) {
			bookStatus = "SALE";
		}
	}
}