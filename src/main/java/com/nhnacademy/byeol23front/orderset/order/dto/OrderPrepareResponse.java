package com.nhnacademy.byeol23front.orderset.order.dto;

import java.math.BigDecimal;

public record OrderPrepareResponse(String orderNumber,
								   BigDecimal actualOrderPrice,
								   String receiver) {
}
