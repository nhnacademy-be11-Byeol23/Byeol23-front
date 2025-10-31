package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;

public record PointOrderResponse(String orderNumber,
								 BigDecimal totalAmount,
								 String method) {

}
