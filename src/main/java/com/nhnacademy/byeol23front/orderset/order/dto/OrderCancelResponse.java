package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;

public record OrderCancelResponse(String orderNumber,
								  BigDecimal actualOrderPrice,
								  String orderStatus) {
}
