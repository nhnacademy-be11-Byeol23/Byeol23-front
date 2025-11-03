package com.nhnacademy.byeol23front.auth;
//TODO: 권한 애노테이션 기능 구현
public @interface Authorized {
	//권한수준이 요구 권한수준보다 높은지 확인
	Role role() default Role.ANONYMOUS;

	//특정 멤버인지 확인 -1L 이면 무시
	long memberId() default -1L;
}
