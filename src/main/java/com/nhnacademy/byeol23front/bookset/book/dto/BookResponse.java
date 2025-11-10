package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.byeol23front.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23front.bookset.tag.dto.AllTagsInfoResponse;

public record BookResponse(
	Long bookId,
	String bookName,
	String toc,
	String description,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	String isbn,
	LocalDate publishDate,
	boolean isPack,
	String bookStatus,
	Integer stock,
	Long publisherId,
	boolean isDeleted,
	List<CategoryLeafResponse> categories,
	List<AllTagsInfoResponse> tags
) {
}