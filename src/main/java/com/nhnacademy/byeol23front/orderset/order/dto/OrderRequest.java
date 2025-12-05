package com.nhnacademy.byeol23front.orderset.order.dto;

import java.util.Map;

// Long -> 도서 아이디, Integer -> 수량
public record OrderRequest(Map<Long, Integer> orderList,
						   Boolean isCartCheckout) {
}