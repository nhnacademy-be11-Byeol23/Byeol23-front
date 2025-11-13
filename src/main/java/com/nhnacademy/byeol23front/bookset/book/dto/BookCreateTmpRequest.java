package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record BookCreateTmpRequest(
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
	List<Long> categoryIds,
	List<Long> tagIds,
	List<Long> contributorIds,
	List<MultipartFile> images
) {
	// null-safe 생성자
	public BookCreateTmpRequest {
		if (images == null) {
			images = List.of();
		}
	}
}

