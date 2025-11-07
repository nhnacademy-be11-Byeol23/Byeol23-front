package com.nhnacademy.byeol23front.cartset.cart.dto;

import java.math.BigDecimal;

public record CartBookResponse(
        Long cartBookId,
        Long bookId,
        String bookName,
        BigDecimal salePrice,
        BigDecimal regularPrice,
        int quantity,
        String imageUrl
) {}