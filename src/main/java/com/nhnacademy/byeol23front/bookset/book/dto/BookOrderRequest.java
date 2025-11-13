package com.nhnacademy.byeol23front.bookset.book.dto;

import java.util.List;

public record BookOrderRequest(List<BookInfoRequest> bookList) {
}