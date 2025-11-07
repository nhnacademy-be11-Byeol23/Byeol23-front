package com.nhnacademy.byeol23front.cartset.cart.dto;

public record CartBookUpdateRequest(
        Long cartBookId,
        Integer quantity
) {
}

