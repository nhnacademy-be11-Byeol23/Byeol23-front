package com.nhnacademy.byeol23front.memberset.member.dto;

import java.util.List;

public record LoginRequestTmp(
	String loginId,
	String loginPassword,
	String redirectUrl,
	List<Long> bookIds,
	List<Integer> quantities
) {}
