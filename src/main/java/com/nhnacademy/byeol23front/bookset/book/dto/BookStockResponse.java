package com.nhnacademy.byeol23front.bookset.book.dto;

public record BookStockResponse(Long bookId,
								String bookName,
								Integer stock) {
}
