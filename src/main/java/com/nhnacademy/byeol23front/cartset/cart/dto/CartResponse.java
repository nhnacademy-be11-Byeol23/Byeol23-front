package com.nhnacademy.byeol23front.cartset.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartBookResponse> cartBooks,
        BigDecimal deliveryFee,
        BigDecimal freeDeliveryCondition
) {
}
