package com.nhnacademy.byeol23front.point.dto;

import lombok.Getter;

@Getter
public enum ReservedPolicy {//enum의 내용 삭제하지 말것 추가만 하세요
	ORDER("주문"),
	REVIEW_WITH_IMAGE("리뷰"),
	REVIEW_WITHOUT_IMAGE("이미지 리뷰"),
	REGISTER("회원가입"),
	UNKNOWN("알수없음"),
	CANCEL("취소");

	private final String description;

	ReservedPolicy(String description) {
		this.description = description;
	}

	// 추가: DB에서 온 문자열을 안전하게 enum으로 변환
	public static ReservedPolicy from(String value) {
		if (value == null) {
			return UNKNOWN;
		}
		try {
			return ReservedPolicy.valueOf(value);
		} catch (IllegalArgumentException ex) {
			// DB에 정의되지 않은 값이면 UNKNOWN 반환
			return UNKNOWN;
		}
	}
}