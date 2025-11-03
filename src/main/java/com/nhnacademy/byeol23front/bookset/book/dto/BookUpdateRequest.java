package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookUpdateRequest(
	String bookName,
	String toc,
	String description,
	BigDecimal regularPrice,
	BigDecimal salePrice,
	LocalDate publishDate,
	boolean isPack,
	String bookStatus,
	Integer stock,
	Long publisherId
) {
}