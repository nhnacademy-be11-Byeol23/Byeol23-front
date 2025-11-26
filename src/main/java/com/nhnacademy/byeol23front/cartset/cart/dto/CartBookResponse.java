package com.nhnacademy.byeol23front.cartset.cart.dto;

public record CartBookResponse(Long bookId, String imageUrl, String bookName, int quantity, int regularPrice, int salePrice) {
}