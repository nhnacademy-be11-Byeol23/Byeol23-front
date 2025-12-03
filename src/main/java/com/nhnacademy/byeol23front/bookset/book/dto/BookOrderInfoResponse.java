package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;

import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingInfoResponse;

public record  BookOrderInfoResponse(Long bookId,
									 String firstImageUrl,
									 String bookTitle,
									 int quantity,
									 BigDecimal price,
									 PackagingInfoResponse packaging) {
}