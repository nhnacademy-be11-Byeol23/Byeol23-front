package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;

public record BookInfoRequest(Long bookId,
							  String bookName,
							  String imageUrl,
							  BigDecimal regularPrice,
							  BigDecimal salePrice,
							  int quantity) {
}
