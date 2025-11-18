package com.nhnacademy.byeol23front.bookset.bookAladin.dto;

import java.util.List;

public record AladinResult (
	String keyword,
	int page,
	int size,
	int lastPage,
	int total,
	List<AladinItem> item
) {
}