package com.nhnacademy.byeol23front.orderset.refund.dto;

public enum RefundOption {
	BREAK("파손, 파본"),
	MIND_CHANGED("단순 변심");

	private final String value;

	RefundOption(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
