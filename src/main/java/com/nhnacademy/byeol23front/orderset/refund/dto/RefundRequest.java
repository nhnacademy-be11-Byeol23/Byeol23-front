package com.nhnacademy.byeol23front.orderset.refund.dto;

import java.math.BigDecimal;

public record RefundRequest(String orderNumber,
							String refundReason,
							RefundOption refundOption,
							BigDecimal appliedFee) {
}
