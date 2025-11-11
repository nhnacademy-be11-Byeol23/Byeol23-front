package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.byeol23front.bookset.category.dto.CategoryLeafResponse;
import com.nhnacademy.byeol23front.bookset.contributor.dto.AllContributorResponse;
import com.nhnacademy.byeol23front.bookset.publisher.dto.AllPublishersInfoResponse;
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
	AllPublishersInfoResponse publisher,
	boolean isDeleted,
	List<CategoryLeafResponse> categories,
	List<AllTagsInfoResponse> tags,
	List<AllContributorResponse> contributors
) {
}