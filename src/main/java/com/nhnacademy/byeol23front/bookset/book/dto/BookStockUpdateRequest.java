package com.nhnacademy.byeol23front.bookset.book.dto;

import jakarta.validation.constraints.Min;

public record BookStockUpdateRequest(@Min(1) Integer stock) {
}
