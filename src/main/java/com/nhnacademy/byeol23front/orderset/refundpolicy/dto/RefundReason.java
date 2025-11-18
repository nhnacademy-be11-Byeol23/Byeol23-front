package com.nhnacademy.byeol23front.orderset.refundpolicy.dto;

public enum RefundReason {
	BREAK("파손, 파지"),
	MIND_CHANGED("단순 변심");

	private final String value;

	RefundReason(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
