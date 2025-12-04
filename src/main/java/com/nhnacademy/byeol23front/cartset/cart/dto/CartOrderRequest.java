package com.nhnacademy.byeol23front.cartset.cart.dto;

import java.util.Map;

// Long -> 도서 아이디, Integer -> 수량
public record CartOrderRequest(Map<Long, Integer> cartOrderList) {
}