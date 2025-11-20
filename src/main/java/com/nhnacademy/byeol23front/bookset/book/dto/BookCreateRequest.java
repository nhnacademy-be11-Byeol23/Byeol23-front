package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookCreateRequest(
	String bookName,
	String toc,
	String description,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	String isbn,
	LocalDate publishDate,
	boolean isPack,
	BookStatus bookStatus,
	Integer stock,
	Long publisherId,
	List<Long> categoryIds,
	List<Long> tagIds,
	List<Long> contributorIds,
	String imageUrl
) {
}