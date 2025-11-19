package com.nhnacademy.byeol23front.bookset.book.dto;

import java.math.BigDecimal;

public record BookOrderInfoResponse(Long bookId,
									String bookTitle,
									int quantity,
									BigDecimal price) {
}
